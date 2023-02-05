package com.safeandfast.controller;

import com.safeandfast.dto.UserDTO;
import com.safeandfast.dto.request.AdminUserUpdateRequest;
import com.safeandfast.dto.request.UpdatePasswordRequest;
import com.safeandfast.dto.request.UserUpdateRequest;
import com.safeandfast.dto.response.ResponseMessage;
import com.safeandfast.dto.response.SFResponse;
import com.safeandfast.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/auth/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<UserDTO> allUsers = userService.getAllUsers();
        return ResponseEntity.ok(allUsers);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<UserDTO> getUser(){
        UserDTO userDTO= userService.getPrincipal();
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/auth/pages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> getAllUsersByPage(@RequestParam("page") int page, @RequestParam("size") int size,
                                                           @RequestParam("sort") String prop,
                                                           @RequestParam(value="direction",required=false,defaultValue="DESC") Direction direction){

        Pageable pageable= PageRequest.of(page, size, Sort.by(direction,prop));
        Page<UserDTO> userDTOPage = userService.getUserPage(pageable);
        return ResponseEntity.ok(userDTOPage);
    }

    @GetMapping("/{id}/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id){
        UserDTO userDTO= userService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    @PatchMapping("/auth")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<SFResponse> updatePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest){

        userService.updatePassword(updatePasswordRequest);

        SFResponse response = new SFResponse();
        response.setMessage(ResponseMessage.PASSWORD_CHANGE_RESPONSE_MESSAGE);
        response.setSuccess(true);
        return ResponseEntity.ok(response);
    }

    //http://localhost:8080/user
    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<SFResponse> updateUser(@Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        userService.updateUser(userUpdateRequest);

        SFResponse response = new SFResponse();
        response.setMessage(ResponseMessage.USER_UPDATE_RESPONSE_MESSAGE);
        response.setSuccess(true);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SFResponse> updateUserAuth(@PathVariable Long id, @Valid @RequestBody AdminUserUpdateRequest adminUserUpdateRequest){

        userService.updateUserAuth(id, adminUserUpdateRequest);

        SFResponse response = new SFResponse();
        response.setMessage(ResponseMessage.USER_UPDATE_RESPONSE_MESSAGE);
        response.setSuccess(true);

        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/{id}/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SFResponse> deleteUser(@PathVariable Long id){
        userService.removeUserById(id);

        SFResponse response = new SFResponse();
        response.setMessage(ResponseMessage.USER_DELETE_RESPONSE_MESSAGE);
        response.setSuccess(true);

        return ResponseEntity.ok(response);
    }




}
