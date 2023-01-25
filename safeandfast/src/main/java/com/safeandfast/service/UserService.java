package com.safeandfast.service;

import com.safeandfast.domain.Role;
import com.safeandfast.domain.User;
import com.safeandfast.domain.enums.RoleType;
import com.safeandfast.dto.UserDTO;
import com.safeandfast.dto.request.RegisterRequest;
import com.safeandfast.exception.ConflictException;
import com.safeandfast.exception.ResourceNotFoundException;
import com.safeandfast.exception.message.ErrorMessage;
import com.safeandfast.mapper.UserMapper;
import com.safeandfast.repository.UserRepository;
import com.safeandfast.security.SecurityUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {


    private UserRepository userRepository;


    private RoleService roleService;


    private PasswordEncoder passwordEncoder;

    private UserMapper userMapper;

    public UserService(UserRepository userRepository, RoleService roleService, @Lazy PasswordEncoder passwordEncoder, UserMapper userMapper){
        this.passwordEncoder=passwordEncoder;
        this.userRepository=userRepository;
        this.roleService=roleService;
        this.userMapper=userMapper;
    }

    public void saveUser(RegisterRequest registerRequest){
        if (userRepository.existsByEmail(registerRequest.getEmail())){
            throw new ConflictException(String.format(ErrorMessage.Email_Already_Exist_Message,registerRequest.getEmail()));
        }
        Role role = roleService.findByType(RoleType.ROLE_CUSTOMER);

        Set<Role> roles = new HashSet<>();
        roles.add(role);

       String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encodedPassword);
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setAddress(registerRequest.getAddress());
        user.setZipCode(registerRequest.getZipCode());
        user.setRoles(roles);

        userRepository.save(user);

        //TODO: we will set other variables for user.
    }

    public User getUserByEmail(String email){
        User user= userRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException(String.format(ErrorMessage.User_Not_Found_Message,email)));
        return user;
    }

    public List<UserDTO> getAllUsers(){
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = userMapper.map(users);
        return userDTOs;
    }

    public UserDTO getPrincipal() {
        User currentUser= getCurrentUser();
        UserDTO userDTO= userMapper.userToUserDTO(currentUser);
        return userDTO;
    }

    public User getCurrentUser() {
        String email= SecurityUtils.getCurrentUserLogin().orElseThrow(()->new ResourceNotFoundException(ErrorMessage.PRINCIPAL_FOUND_MESSAGE));
        User user=getUserByEmail(email);
        return user;
    }

    public UserDTO getUserById(Long id) {
        User user=userRepository.findById(id).orElseThrow(()->new
                ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE, id)));
        return userMapper.userToUserDTO(user);
    }

}
