package com.safeandfast.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.safeandfast.domain.ContactMessage;
import com.safeandfast.dto.ContactMessageDTO;
import com.safeandfast.dto.request.ContactMessageRequest;


import java.util.List;

@Mapper(componentModel = "spring")
public interface ContactMessageMapper {

    @Mapping(target="id",ignore=true)
    ContactMessage contactMessageRequestToContactMessage(ContactMessageRequest contactMessageRequest);


    ContactMessageDTO contactMessageToDTO(ContactMessage contactMessage);
    List<ContactMessageDTO> map(List<ContactMessage> contactMessageList);
}

