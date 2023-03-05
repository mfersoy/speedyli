package com.safeandfast.service;

import com.safeandfast.domain.Car;
import com.safeandfast.domain.ImageFile;
import com.safeandfast.dto.CarDTO;
import com.safeandfast.exception.BadRequestException;
import com.safeandfast.exception.ConflictException;
import com.safeandfast.exception.ResourceNotFoundException;
import com.safeandfast.exception.message.ErrorMessage;
import com.safeandfast.mapper.CarMapper;
import com.safeandfast.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarMapper carMapper;

    @Autowired
   private ImageFileService imageFileService;

    @Autowired
    private ReservationService reservationService;



    public void saveCar(String imageId, CarDTO carDTO){
        ImageFile imageFile = imageFileService.findImageById(imageId);

        Integer usedCarCount = carRepository.findCarsByImageId(imageFile.getId()).size();

        if (usedCarCount > 0) {
            throw new ConflictException(ErrorMessage.IMAGE_USED_MESSAGE);
        }

        Car car = carMapper.carDTOToCar(carDTO);
        Set<ImageFile> imFiles = new HashSet<>();
        imFiles.add(imageFile);
        car.setImage(imFiles);

        carRepository.save(car);
    }

    public List<CarDTO> getAllCars(){

        List<Car> carList = carRepository.findAll();
        return carMapper.map(carList);

    }

    public Page<CarDTO> findAllWithPage(Pageable pageable) {
        Page<Car> carPage = carRepository.findAll(pageable);
        Page<CarDTO> carPageDTO = carPage.map(new Function<Car, CarDTO>() {
            @Override
            public CarDTO apply(Car car) {
                // TODO Auto-generated method stub
                return carMapper.carToCarDTO(car);
            }
        });
        return carPageDTO;
    }

    public CarDTO findById(Long id) {
        Car car = getCar(id);
        return carMapper.carToCarDTO(car);
    }

    public Car getCar(Long id) {
        Car car = carRepository.findCarById(id).orElseThrow(() -> new
                ResourceNotFoundException(String.format(ErrorMessage.Resoruce_Not_Found_Message, id)));
        return car;
    }

    public void updateCar(Long id, String imageId, CarDTO carDTO) {
        Car car = getCar(id);

        if (car.getBuiltIn()) {
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }

        ImageFile imageFile = imageFileService.findImageById(imageId);
        List<Car> carList = carRepository.findCarsByImageId(imageFile.getId());

        for (Car c : carList) {
            if (car.getId().longValue() != c.getId().longValue()) {
                throw new ConflictException(ErrorMessage.IMAGE_USED_MESSAGE);
            }
        }

        car.setAge(carDTO.getAge());
        car.setAirConditioning(carDTO.getAirConditioning());
        car.setBuiltIn(carDTO.getBuiltIn());
        car.setDoors(carDTO.getDoors());
        car.setFuelType(carDTO.getFuelType());
        car.setLuggage(carDTO.getLuggage());
        car.setModel(carDTO.getModel());
        car.setPricePerHour(carDTO.getPricePerHour());
        car.setSeats(carDTO.getSeats());
        car.setTransmission(carDTO.getTransmission());

        car.getImage().add(imageFile);

        carRepository.save(car);
    }

    public void removeById(Long id) {
        Car car = getCarById(id);

        if (car.getBuiltIn()) {
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }

       boolean exists = reservationService.existsByCar(car);

        if (exists){
            throw  new BadRequestException(ErrorMessage.CAR_USED_BY_RESERVATION_MESSAGE);
        }

        carRepository.delete(car);
    }

    public Car getCarById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new
                ResourceNotFoundException(String.format(ErrorMessage.Resoruce_Not_Found_Message, id)));
        return car;
    }

    public List<Car> getAllCar() {

        return carRepository.getAllBy();
    }

}
