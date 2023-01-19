package com.safeandfast.controller;

import com.safeandfast.dto.request.LoginRequest;
import com.safeandfast.dto.request.RegisterRequest;
import com.safeandfast.dto.response.LoginResponse;
import com.safeandfast.dto.response.ResponseMessage;
import com.safeandfast.dto.response.SFResponse;
import com.safeandfast.security.jwt.JwtUtils;
import com.safeandfast.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserJWTController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<SFResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest){
       userService.saveUser(registerRequest);

       SFResponse sfResponse = new SFResponse();
       sfResponse.setMessage(ResponseMessage.REGISTER_RESPONSE_MESSAGE);
       sfResponse.setSuccess(true);
       return new ResponseEntity<>(sfResponse, HttpStatus.CREATED) ;

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginRequest loginRequest){

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword());

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        UserDetails userDetails= (UserDetails) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateJwtToken(userDetails);


        LoginResponse loginResponse=new LoginResponse(jwtToken);

        return new ResponseEntity<>(loginResponse,HttpStatus.OK);

    }

}