package com.safeandfast.service;

import com.safeandfast.domain.ContactMessage;
import com.safeandfast.exception.ResourceNotFoundException;
import com.safeandfast.exception.message.ErrorMessage;
import com.safeandfast.repository.ContactMessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ContactMessageService {

    private ContactMessageRepository contactMessageRepository;

  public void saveMessage(ContactMessage contactMessage){

        contactMessageRepository.save(contactMessage);
  }

  public  List<ContactMessage> getAll(){
     return contactMessageRepository.findAll();
  }

    public Page<ContactMessage> getAll(Pageable pageable){
        return contactMessageRepository.findAll(pageable);
    }

    public  ContactMessage getContactMessage(Long id){
      ContactMessage contactMessage= contactMessageRepository.findById(id).orElseThrow(()-> new
              ResourceNotFoundException(String.format(ErrorMessage.Resoruce_Not_Found_Message,id)));

      return contactMessage;
    }

    public  void deleteContactMessage(Long id) {
      ContactMessage message= getContactMessage(id);
      contactMessageRepository.delete(message);
    }

    public void updateContactMessage(Long id, ContactMessage contactMessage){
      ContactMessage foundContactMessage = getContactMessage(id);
      foundContactMessage.setName(contactMessage.getName());
      foundContactMessage.setSubject(contactMessage.getSubject());
      foundContactMessage.setEmail(contactMessage.getEmail());
      foundContactMessage.setBody(contactMessage.getBody());

      contactMessageRepository.save(foundContactMessage);
    }













}
