package com.safeandfast.service;

import com.safeandfast.domain.Car;
import com.safeandfast.domain.Reservation;
import com.safeandfast.domain.User;
import com.safeandfast.exception.message.ErrorMessage;
import com.safeandfast.report.ExcelReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ReportService {
    @Autowired
    private UserService userService;

    @Autowired
    private CarService carService;

    @Autowired
    private ReservationService reservationService;

    public ByteArrayInputStream getUserReport() {
        List<User> users = userService.getUsers();
        try {
            return ExcelReporter.getUserExcelReport(users);
        } catch (IOException e) {
            throw new RuntimeException(ErrorMessage.EXCEL_REPORT_ERROR_MESSAGE);
        }
    }

    public ByteArrayInputStream getCarReport() {
        List<Car> cars = carService.getAllCar();
        try {
            return ExcelReporter.getCarExcelReport(cars);
        } catch (IOException e) {
            throw new RuntimeException(ErrorMessage.EXCEL_REPORT_ERROR_MESSAGE);
        }
    }

    public ByteArrayInputStream getReservationReport() {
        List<Reservation> reservations = reservationService.getAll();
        try {
            return ExcelReporter.getReservationExcelReport(reservations);
        } catch (IOException e) {
            throw new RuntimeException(ErrorMessage.EXCEL_REPORT_ERROR_MESSAGE);
        }
    }
}

