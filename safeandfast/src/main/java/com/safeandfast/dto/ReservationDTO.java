package com.safeandfast.dto;

import com.safeandfast.domain.enums.ReservationStatus;

import java.time.LocalDateTime;

public class ReservationDTO {

    private Long id;

    private CarDTO car;

    private Long userId;

    private LocalDateTime pickUpTime;

    private LocalDateTime dropOffTime;

    private String pickUpLocation;

    private String dropOffLocation;

    private ReservationStatus status;

    private Double totalPrice;
}
