package com.safeandfast.controller;

import com.safeandfast.dto.CarDTO;
import com.safeandfast.dto.response.ResponseMessage;
import com.safeandfast.dto.response.SFResponse;
import com.safeandfast.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/car")
public class CarController {

    @Autowired
    private CarService carService;

    @PostMapping("/admin/{imageId}/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SFResponse> saveCar(@PathVariable String imageId, @Valid  @RequestBody CarDTO carDTO){

        carService.saveCar(imageId,carDTO);
        SFResponse sfResponse= new SFResponse(ResponseMessage.CAR_SAVED_RESPONSE_MESSAGE,true);

        return new ResponseEntity<>(sfResponse, HttpStatus.CREATED);
    }






}
