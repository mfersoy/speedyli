package com.safeandfast.service;

import com.safeandfast.domain.Car;
import com.safeandfast.domain.ImageFile;
import com.safeandfast.dto.CarDTO;
import com.safeandfast.exception.ConflictException;
import com.safeandfast.exception.ResourceNotFoundException;
import com.safeandfast.exception.message.ErrorMessage;
import com.safeandfast.mapper.CarMapper;
import com.safeandfast.repository.CarRepository;
import com.safeandfast.repository.ImageFileRepository;
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



    public void saveCar(String imageId, CarDTO carDTO){
        ImageFile imageFile = imageFileService.findImageById(imageId);

        Integer usedCarCount = carRepository.findCarCountByImageId(imageFile.getId());

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

}
