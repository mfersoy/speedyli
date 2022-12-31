package com.safeandfast.service;

import com.safeandfast.domain.Role;
import com.safeandfast.domain.User;
import com.safeandfast.domain.enums.RoleType;
import com.safeandfast.exception.ResourceNotFoundException;
import com.safeandfast.exception.message.ErrorMessage;
import com.safeandfast.repository.RoleRepository;
import com.safeandfast.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role findByType(RoleType roleType){

   Role role= roleRepository.findByType(roleType).orElseThrow(()->new
           ResourceNotFoundException(String.format(ErrorMessage.Role_Not_Found_Message,roleType.name())));
        return role;
    }


}
