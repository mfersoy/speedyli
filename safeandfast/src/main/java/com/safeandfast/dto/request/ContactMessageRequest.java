package com.safeandfast.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactMessageRequest {
    @Size(min=1,max=50,message="Your Name '${validatedValue}' must be between {min} and {max} chars long")
    @NotBlank(message="Please provide a Name")
    private String name;

    @Size(min=5,max=50,message="Subject '${validatedValue}' must be between {min} and {max} chars long")
    @NotBlank(message="Please provide a Subject")
    private String subject;

    @Size(min=20,max=200,message="Message Body '${validatedValue}' must be between {min} and {max} chars long")
    @NotBlank(message="Please provide a Message Body")
    private String body;

    @Email(message="Please provide a valid Email")
    private String email;

}
