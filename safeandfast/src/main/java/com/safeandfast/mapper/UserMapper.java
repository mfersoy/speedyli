package com.safeandfast.mapper;

import com.safeandfast.domain.User;
import com.safeandfast.dto.UserDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel="spring")
public interface UserMapper {

    UserDTO userToUserDTO(User user);

    List<UserDTO> map(List<User> userList);




}
