package com.safeandfast.service;
import com.safeandfast.domain.Car;
import com.safeandfast.domain.Reservation;
import com.safeandfast.domain.User;
import com.safeandfast.domain.enums.ReservationStatus;
import com.safeandfast.dto.ReservationDTO;
import com.safeandfast.dto.request.ReservationRequest;
import com.safeandfast.dto.request.ReservationUpdateRequest;
import com.safeandfast.exception.BadRequestException;
import com.safeandfast.exception.ResourceNotFoundException;
import com.safeandfast.exception.message.ErrorMessage;
import com.safeandfast.mapper.ReservationMapper;
import com.safeandfast.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Function;

@Service
public class ReservationService {


    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationMapper reservationMapper;

    public boolean existsByUser(User user) {

        return reservationRepository.existsByUser(user);
    }

    public boolean existsByCar(Car car) {

        return reservationRepository.existsByCar(car);
    }

    public List<Reservation> getAll() {
        return reservationRepository.findAllBy();
    }


    public void createReservation(ReservationRequest reservationRequest, User user, Car car) {

        checkReservationTimeIsCorrect(reservationRequest.getPickUpTime(), reservationRequest.getDropOffTime());
        boolean carStatus = checkCarAvailability(car, reservationRequest.getPickUpTime(), reservationRequest.getDropOffTime());
        Reservation reservation = reservationMapper.reservationRequestToReservation(reservationRequest);

        if (carStatus) {
            reservation.setStatus(ReservationStatus.CREATED);
        } else {
            throw new BadRequestException(ErrorMessage.CAR_NOT_AVAILABLE_MESSAGE);
        }

        reservation.setCar(car);
        reservation.setUser(user);

        Double totalPrice = getTotalPrice(car, reservationRequest.getPickUpTime(), reservationRequest.getDropOffTime());

        reservation.setTotalPrice(totalPrice);
        reservationRepository.save(reservation);
    }

    private void checkReservationTimeIsCorrect(LocalDateTime pickUpTime, LocalDateTime dropOffTime) {
        LocalDateTime now = LocalDateTime.now();

        if (pickUpTime.isBefore(now)) {
            throw new BadRequestException(ErrorMessage.RESERVATION_TIME_INCORRECT_MESSAGE);
        }

        boolean isEqual = pickUpTime.isEqual(dropOffTime) ? true : false;
        boolean isBefore = pickUpTime.isBefore(dropOffTime) ? true : false;

        if (isEqual || !isBefore) {
            throw new BadRequestException(ErrorMessage.RESERVATION_TIME_INCORRECT_MESSAGE);
        }
    }

    public boolean checkCarAvailability(Car car, LocalDateTime pickUpTime, LocalDateTime dropOffTime) {
        List<Reservation> existReservations = getConflictReservations(car, pickUpTime, dropOffTime);

        return existReservations.isEmpty();
    }

    private List<Reservation> getConflictReservations(Car car, LocalDateTime pickUpTime, LocalDateTime dropOffTime) {
        if (pickUpTime.isAfter(dropOffTime)) {
            throw new BadRequestException(ErrorMessage.RESERVATION_TIME_INCORRECT_MESSAGE);
        }

        ReservationStatus[] status = {ReservationStatus.CANCELLED, ReservationStatus.DONE};

        List<Reservation> existReservations = reservationRepository.checkCarStatus(car.getId(), pickUpTime, dropOffTime, status);
        return existReservations;
    }

    public Double getTotalPrice(Car car, LocalDateTime pickUpTime, LocalDateTime dropOffTime) {
        Long minutes = ChronoUnit.MINUTES.between(pickUpTime, dropOffTime);
        double hours = Math.ceil(minutes / 60.0);
        return car.getPricePerHour() * hours;
    }

    public void updateReservation(Long reservationId, Car car, ReservationUpdateRequest reservationUpdateRequest) {
        Reservation reservation = getById(reservationId);

        if (reservation.getStatus().equals(ReservationStatus.CANCELLED) || reservation.getStatus().equals(ReservationStatus.DONE)) {
            throw new BadRequestException(ErrorMessage.RESERVATION_STATUS_CANT_CHANGE_MESSAGE);
        }

        //statusu CANCELLED, DONE yaparken zaman valid mi diye kontrol etmesin
        if (reservationUpdateRequest.getStatus() != null && reservationUpdateRequest.getStatus() == ReservationStatus.CREATED) {
            checkReservationTimeIsCorrect(reservationUpdateRequest.getPickUpTime(), reservationUpdateRequest.getDropOffTime());

            List<Reservation> conflictReservations = getConflictReservations(car, reservationUpdateRequest.getPickUpTime(), reservationUpdateRequest.getDropOffTime());

            if (!conflictReservations.isEmpty()) {
                if (!(conflictReservations.size() == 1 && conflictReservations.get(0).getId().equals(reservationId))) {
                    throw new BadRequestException(ErrorMessage.CAR_NOT_AVAILABLE_MESSAGE);
                }
            }

            Double totalPrice = getTotalPrice(car, reservationUpdateRequest.getPickUpTime(), reservationUpdateRequest.getDropOffTime());
            reservation.setTotalPrice(totalPrice);
            reservation.setCar(car);
        }

        reservation.setPickUpTime(reservationUpdateRequest.getPickUpTime());
        reservation.setDropOffTime(reservationUpdateRequest.getDropOffTime());
        reservation.setPickUpLocation(reservationUpdateRequest.getPickUpLocation());
        reservation.setDropOffLocation(reservationUpdateRequest.getDropOffLocation());
        reservation.setStatus(reservationUpdateRequest.getStatus());
        reservationRepository.save(reservation);
    }

    public List<ReservationDTO> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservationMapper.map(reservations);
    }
    public Page<ReservationDTO> getReservationPage(Pageable pageable) {
        Page<Reservation> reservationPage = reservationRepository.findAll(pageable);

        return getReservationDTOPage(reservationPage);
    }

    private Page<ReservationDTO> getReservationDTOPage(Page<Reservation> reservationPage) {
        Page<ReservationDTO> reservationDTOPage = reservationPage.map(new Function<Reservation, ReservationDTO>() {
            @Override
            public ReservationDTO apply(Reservation reservation) {
                return reservationMapper.reservationToReservationDTO(reservation);
            }
        });
        return reservationDTOPage;
    }

    public Reservation getById(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new
                ResourceNotFoundException(String.format(ErrorMessage.Resoruce_Not_Found_Message, id)));
        return reservation;
    }

    public ReservationDTO getReservationDTO(Long id) {
        Reservation reservation = getById(id);
        return reservationMapper.reservationToReservationDTO(reservation);
    }

    public Page<ReservationDTO> findReservationPageByUser(User user, Pageable pageable) {
        Page<Reservation> reservationPage = reservationRepository.findAllByUser(user, pageable);
        return getReservationDTOPage(reservationPage);
    }

    public ReservationDTO findByIdAndUser(Long id, User user) {
        Reservation reservation = reservationRepository.findByIdAndUser(id, user).orElseThrow(() -> new
                ResourceNotFoundException(String.format(ErrorMessage.Resoruce_Not_Found_Message, id)));

        return reservationMapper.reservationToReservationDTO(reservation);
    }

    public void removeById(Long id) {
        boolean exist = reservationRepository.existsById(id);
        if (!exist) {
            throw new ResourceNotFoundException(String.format(ErrorMessage.Resoruce_Not_Found_Message, id));
        }

        reservationRepository.deleteById(id);
    }






}
