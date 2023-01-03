package com.safeandfast.controller;

import com.safeandfast.dto.request.RegisterRequest;
import com.safeandfast.dto.response.SFResponse;
import com.safeandfast.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserJWTController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<SFResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest){
        return null;

    }

}