package com.safeandfast.service;

import com.safeandfast.domain.Car;
import com.safeandfast.domain.ImageFile;
import com.safeandfast.dto.CarDTO;
import com.safeandfast.exception.ConflictException;
import com.safeandfast.exception.message.ErrorMessage;
import com.safeandfast.mapper.CarMapper;
import com.safeandfast.repository.CarRepository;
import com.safeandfast.repository.ImageFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
   private ImageFileService imageFileService;

    @Autowired
    private CarMapper carMapper;
    public void saveCar(String ImageId, CarDTO carDTO){
        ImageFile imageFile = imageFileService.findImageById(ImageId);

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

}
