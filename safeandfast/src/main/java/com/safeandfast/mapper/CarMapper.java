package com.safeandfast.mapper;

import com.safeandfast.domain.Car;
import com.safeandfast.dto.CarDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CarMapper {

    @Mapping(target = "image", ignore = true)
    Car carDTOToCar(CarDTO carDTO);
}
