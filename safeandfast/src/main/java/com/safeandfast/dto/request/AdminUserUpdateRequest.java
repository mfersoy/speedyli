package com.safeandfast.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserUpdateRequest {

    @Size(max = 50)
    @NotBlank(message = "Please provide your First name")
    private String firstName;

    @Size(max = 50)
    @NotBlank(message = "Please provide your Last name")
    private String lastName;

    @Size(min = 4, max = 20,message="Please Provide Correct Size for Password")
    private String password;

    @Size(min= 5, max = 80)
    @NotBlank(message = "Please provide your email")
    private String email;

    @Size(min = 14, max = 14)
    @NotBlank(message = "Please provide your adress")
    private String phoneNumber;

    @Size(max = 100)
    @NotBlank(message = "Please provide your adress")
    private String address;

    @Size(max = 15)
    @NotBlank(message = "Please provide your zipCode")
    private String zipCode;

    private Boolean buildIn;

    private Set<String> roles;
}
