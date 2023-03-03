package com.safeandfast.controller;

import com.safeandfast.domain.Car;
import com.safeandfast.domain.User;
import com.safeandfast.dto.request.ReservationRequest;
import com.safeandfast.dto.response.ResponseMessage;
import com.safeandfast.dto.response.SFResponse;
import com.safeandfast.service.CarService;
import com.safeandfast.service.ReservationService;
import com.safeandfast.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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




}