package com.safeandfast.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.safeandfast.dto.request.AdminUserUpdateRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safeandfast.domain.Role;
import com.safeandfast.domain.User;
import com.safeandfast.domain.enums.RoleType;
import com.safeandfast.dto.UserDTO;
import com.safeandfast.dto.request.RegisterRequest;
import com.safeandfast.dto.request.UpdatePasswordRequest;
import com.safeandfast.dto.request.UserUpdateRequest;
import com.safeandfast.exception.BadRequestException;
import com.safeandfast.exception.ConflictException;
import com.safeandfast.exception.ResourceNotFoundException;
import com.safeandfast.exception.message.ErrorMessage;
import com.safeandfast.mapper.UserMapper;
import com.safeandfast.repository.UserRepository;
import com.safeandfast.security.SecurityUtils;

@Service
public class UserService {

    private  UserRepository userRepository;


    private RoleService roleService;

    private PasswordEncoder passwordEncoder;

    private UserMapper userMapper;

    private ReservationService reservationService;

    //private ReservationService reservationService;

    public UserService(UserRepository userRepository, RoleService roleService, @Lazy PasswordEncoder passwordEncoder, UserMapper userMapper, ReservationService reservationService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.reservationService = reservationService;
     //   this.reservationService = reservationService;
    }

    public void saveUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ConflictException(String.format(ErrorMessage.Email_Already_Exist_Message, registerRequest.getEmail()));
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
    }

    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new
                ResourceNotFoundException(String.format(ErrorMessage.User_Not_Found_Message, email)));
        return user;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = userMapper.map(users);
        return userDTOs;
    }


    public UserDTO getPrincipal() {
        User currentUser = getCurrentUser();
        UserDTO userDTO = userMapper.userToUserDTO(currentUser);
        return userDTO;
    }

    public User getCurrentUser() {
        String email = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.PRINCIPAL_FOUND_MESSAGE));
        User user = getUserByEmail(email);
        return user;
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new
                ResourceNotFoundException(String.format(ErrorMessage.Resoruce_Not_Found_Message, id)));
        return userMapper.userToUserDTO(user);
    }

    public Page<UserDTO> getUserPage(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);

        return getUserDTOPage(userPage);
    }

    public void updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        User user = getCurrentUser();

        //Builtin attribute: Datalarının Değişmesi istenmeyen bir objenin builtIn değeri true olur.
        if (user.getBuildIn()) {
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }

        if (!passwordEncoder.matches(updatePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new BadRequestException(ErrorMessage.PASSWORD_NOT_MATCH);
        }

        String hashedPassword = passwordEncoder.encode(updatePasswordRequest.getNewPassword());

        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(UserUpdateRequest userUpdateRequest) {
        User user = getCurrentUser();

        if (user.getBuildIn()) {
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }

        boolean emailExist = userRepository.existsByEmail(userUpdateRequest.getEmail());

        if (emailExist && !userUpdateRequest.getEmail().equals(user.getEmail())) {
            throw new ConflictException(String.format(ErrorMessage.Email_Already_Exist_Message, userUpdateRequest.getEmail()));
        }

        userRepository.update(user.getId(), userUpdateRequest.getFirstName(), userUpdateRequest.getLastName(),
                userUpdateRequest.getPhoneNumber(), userUpdateRequest.getEmail(), userUpdateRequest.getAddress(), userUpdateRequest.getZipCode());
    }

   public void updateUserAuth(Long id, AdminUserUpdateRequest adminUserUpdateRequest) {
       User user = getById(id);

      if (user.getBuildIn()) {
           throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
       }

        boolean emailExist = userRepository.existsByEmail(adminUserUpdateRequest.getEmail());

       if (emailExist && !adminUserUpdateRequest.getEmail().equals(user.getEmail())) {
           throw new ConflictException(String.format(ErrorMessage.Email_Already_Exist_Message, adminUserUpdateRequest.getEmail()));
       }

        if (adminUserUpdateRequest.getPassword() == null) {
            adminUserUpdateRequest.setPassword(user.getPassword());
        } else {
           String encodedPassword = passwordEncoder.encode(adminUserUpdateRequest.getPassword());
            adminUserUpdateRequest.setPassword(encodedPassword);
        }

        Set<String> userStrRoles = adminUserUpdateRequest.getRoles();
        Set<Role> roles = convertRoles(userStrRoles);

        user.setFirstName(adminUserUpdateRequest.getFirstName());
       user.setLastName(adminUserUpdateRequest.getLastName());
       user.setEmail(adminUserUpdateRequest.getEmail());
        user.setPassword(adminUserUpdateRequest.getPassword());
        user.setPhoneNumber(adminUserUpdateRequest.getPhoneNumber());
       user.setAddress(adminUserUpdateRequest.getAddress());
       user.setZipCode(adminUserUpdateRequest.getZipCode());
        user.setBuildIn(adminUserUpdateRequest.getBuildIn());

        user.setRoles(roles);

        userRepository.save(user);

    }

    public User getById(Long id) {
        User user = userRepository.findUserById(id).orElseThrow(() -> new
                ResourceNotFoundException(String.format(ErrorMessage.Resoruce_Not_Found_Message, id)));
        return user;
    }

    public void removeUserById(Long id) {
        User user = getById(id);

        if (user.getBuildIn()) {
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }
        boolean exists = reservationService.existsByUser(user);
        if (exists) {
            throw new BadRequestException(ErrorMessage.USER_USED_BY_RESERVATION_MESSAGE);
        }

        userRepository.deleteById(user.getId());
    }

    public Set<Role> convertRoles(Set<String> pRoles) {
        Set<Role> roles = new HashSet<>();

        if (pRoles == null) {
            Role userRole = roleService.findByType(RoleType.ROLE_CUSTOMER);
            roles.add(userRole);
        } else {
            pRoles.forEach(roleStr -> {
                if (roleStr.equals(RoleType.ROLE_ADMIN.getName())) {
                    Role adminRole = roleService.findByType(RoleType.ROLE_ADMIN);
                    roles.add(adminRole);
                } else {
                    Role userRole = roleService.findByType(RoleType.ROLE_CUSTOMER);
                    roles.add(userRole);
                }
            });
        }
        return roles;
    }

    private Page<UserDTO> getUserDTOPage(Page<User> userPage) {
        Page<UserDTO> userDTOPage = userPage.map(new Function<User, UserDTO>() {
            @Override
            public UserDTO apply(User user) {
                return userMapper.userToUserDTO(user);
            }
        });
        return userDTOPage;
    }
}