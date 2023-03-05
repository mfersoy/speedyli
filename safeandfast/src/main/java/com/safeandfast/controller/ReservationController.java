package com.safeandfast.controller;

import com.safeandfast.domain.Car;
import com.safeandfast.domain.User;
import com.safeandfast.dto.ReservationDTO;
import com.safeandfast.dto.request.ReservationRequest;
import com.safeandfast.dto.request.ReservationUpdateRequest;
import com.safeandfast.dto.response.CarAvailabilityResponse;
import com.safeandfast.dto.response.ResponseMessage;
import com.safeandfast.dto.response.SFResponse;
import com.safeandfast.service.CarService;
import com.safeandfast.service.ReservationService;
import com.safeandfast.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    @Autowired
    private CarService carService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<SFResponse> makeReservation(@RequestParam("carId") Long carId, @Valid @RequestBody ReservationRequest reservationRequest) {
        Car car = carService.getCarById(carId);
        User user = userService.getCurrentUser();
        reservationService.createReservation(reservationRequest, user, car);
        SFResponse response = new SFResponse(ResponseMessage.RESERVATION_CREATED_RESPONSE_MESSAGE, true);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/add/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SFResponse> addReservation(@RequestParam("userId") Long userId, @RequestParam("carId") Long carId, @Valid @RequestBody ReservationRequest reservationRequest) {
        Car car = carService.getCarById(carId);
        User user = userService.getById(userId);
        reservationService.createReservation(reservationRequest, user, car);
        SFResponse response = new SFResponse(ResponseMessage.RESERVATION_CREATED_RESPONSE_MESSAGE, true);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/admin/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SFResponse> updateReservation(@RequestParam("carId") Long carId,
                                                        @RequestParam("reservationId") Long reservationId, @Valid @RequestBody ReservationUpdateRequest reservationUpdateRequest) {
        Car car = carService.getCarById(carId);
        reservationService.updateReservation(reservationId, car, reservationUpdateRequest);
        SFResponse response = new SFResponse(ResponseMessage.RESERVATION_UPDATED_RESPONSE_MESSAGE, true);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/auth")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<SFResponse> checkCarIsAvailable(@RequestParam("carId") Long carId,
                                                          @RequestParam("pickUpDateTime") @DateTimeFormat(pattern = "MM/dd/yyyy HH:mm:ss") LocalDateTime pickUpTime,
                                                          @RequestParam("dropOffDateTime") @DateTimeFormat(pattern = "MM/dd/yyyy HH:mm:ss") LocalDateTime dropOffTime) {
        Car car = carService.getCarById(carId);
        boolean isAvailable = reservationService.checkCarAvailability(car, pickUpTime, dropOffTime);
        Double totalPrice = reservationService.getTotalPrice(car, pickUpTime, dropOffTime);
        SFResponse response = new CarAvailabilityResponse(ResponseMessage.CAR_AVAILABLE_MESSAGE, true, isAvailable, totalPrice);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<ReservationDTO> allReservations = reservationService.getAllReservations();

        return ResponseEntity.ok(allReservations);
    }

    @GetMapping("/admin/all/pages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReservationDTO>> getAllReservationsWithPage(@RequestParam("page") int page,
                                                                           @RequestParam("size") int size,
                                                                           @RequestParam("sort") String prop,
                                                                           @RequestParam(value = "direction", required = false, defaultValue = "DESC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, prop));
        Page<ReservationDTO> allReservations = reservationService.getReservationPage(pageable);

        return ResponseEntity.ok(allReservations);
    }

    //Admin herhangi bir reservation id ile bir reservasyon bilgisini almak için kullanıyor.
    @GetMapping("/{id}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id) {
        ReservationDTO reservationDTO = reservationService.getReservationDTO(id);

        return ResponseEntity.ok(reservationDTO);
    }

    //Admin bir userId ile o user'a ait olan tüm reservasyonları getirecek
    @GetMapping("/admin/auth/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReservationDTO>> getAllUserReservations(@RequestParam("userId") Long userId, @RequestParam("page") int page,
                                                                       @RequestParam("size") int size,
                                                                       @RequestParam("sort") String prop,
                                                                       @RequestParam(value = "direction", required = false, defaultValue = "DESC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, prop));
        User user = userService.getById(userId);
        Page<ReservationDTO> reservationDTOPage = reservationService.findReservationPageByUser(user, pageable);

        return ResponseEntity.ok(reservationDTOPage);
    }

    //current user kendisine ait olan bir reservasyon bilgisi getirmek isterse
    @GetMapping("/{id}/auth")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<ReservationDTO> getUserReservationById(@PathVariable Long id) {
        User user = userService.getCurrentUser();
        ReservationDTO reservationDTO = reservationService.findByIdAndUser(id, user);

        return ResponseEntity.ok(reservationDTO);
    }


    //current user kendisine ait olan bir reservasyon bilgisi getirmek isterse
    @GetMapping("/auth/all")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<Page<ReservationDTO>> getAllUserReservations(@RequestParam("page") int page,
                                                                       @RequestParam("size") int size,
                                                                       @RequestParam("sort") String prop,
                                                                       @RequestParam(value = "direction", required = false, defaultValue = "DESC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, prop));
        User user = userService.getCurrentUser();
        Page<ReservationDTO> reservationDTOPage = reservationService.findReservationPageByUser(user, pageable);

        return ResponseEntity.ok(reservationDTOPage);
    }

    @DeleteMapping("/admin/{id}/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SFResponse> deleteReservation(@PathVariable Long id) {
        reservationService.removeById(id);
        SFResponse response = new SFResponse(ResponseMessage.RESERVATION_DELETED_RESPONSE_MESSAGE, true);

        return ResponseEntity.ok(response);
    }

}