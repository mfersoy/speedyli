package com.safeandfast.service;

import com.safeandfast.domain.Role;
import com.safeandfast.domain.User;
import com.safeandfast.domain.enums.RoleType;
import com.safeandfast.dto.request.RegisterRequest;
import com.safeandfast.exception.ConflictException;
import com.safeandfast.exception.ResourceNotFoundException;
import com.safeandfast.exception.message.ErrorMessage;
import com.safeandfast.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveUser(RegisterRequest registerRequest){
        if (userRepository.existsByEmail(registerRequest.getEmail())){
            throw new ConflictException(String.format(ErrorMessage.Email_Already_Exist_Message,registerRequest.getEmail()));
        }
        Role role = roleService.findByType(RoleType.ROLE_CUSTOMER);

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        //TODO: we will set other variables for user.
    }

    public User getUserByEmail(String email){
        User user= userRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException(String.format(ErrorMessage.User_Not_Found_Message,email)));
        return user;
    }
}
