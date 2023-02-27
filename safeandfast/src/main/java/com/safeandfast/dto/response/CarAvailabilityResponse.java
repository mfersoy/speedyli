package com.safeandfast.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CarAvailabilityResponse extends SFResponse{

    private boolean available;

    private double totalPrice;

    public CarAvailabilityResponse(String message, boolean success, boolean available, double totalPrice) {
        super(message, success);
        this.available = available;
        this.totalPrice = totalPrice;
    }
}
