package com.safeandfast.controller;

import com.safeandfast.domain.ContactMessage;
import com.safeandfast.dto.ContactMessageDTO;
import com.safeandfast.dto.request.ContactMessageRequest;
import com.safeandfast.dto.response.ResponseMessage;
import com.safeandfast.dto.response.SFResponse;
import com.safeandfast.mapper.ContactMessageMapper;
import com.safeandfast.service.ContactMessageService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.function.Function;


@RestController
@RequestMapping("/contactmessage")
@AllArgsConstructor
public class ContactMessageController {

    private ContactMessageService contactMessageService;

    private ContactMessageMapper contactMessageMapper;

    @PostMapping("/visitors")
    public ResponseEntity<SFResponse> createMessage(@Valid @RequestBody ContactMessageRequest contactMessageRequest) {

        ContactMessage contactMessage = contactMessageMapper.contactMessageRequestToContactMessage(contactMessageRequest);
        contactMessageService.saveMessage(contactMessage);

        SFResponse responce = new SFResponse(ResponseMessage.CONTACTMESSAGE_SAVE_RESPONSE_MESSAGE, true);

        return new ResponseEntity<>(responce, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ContactMessageDTO>> getAllContactMessage() {
        List<ContactMessage> contactMessagesList = contactMessageService.getAll();
        //ContactMessage->ContactMessageDTO
        List<ContactMessageDTO> contactMessageDTOList = contactMessageMapper.map(contactMessagesList);

        return ResponseEntity.ok(contactMessageDTOList);
    }

    @GetMapping("/pages")
    public ResponseEntity<Page<ContactMessageDTO>> getAllContactMessageWithPage(@RequestParam("page") int page,@RequestParam("size") int size,
                                                                                @RequestParam("sort") String prop,
                                                                                @RequestParam(value="direction",required=false,defaultValue="DESC") Direction direction){

        Pageable pageable=PageRequest.of(page, size,Sort.by(direction,prop));

        Page<ContactMessage> contactMessagePage = contactMessageService.getAll(pageable);

        Page<ContactMessageDTO> pageDTO = getPageDTO(contactMessagePage);

        return ResponseEntity.ok(pageDTO);
    }

    private Page<ContactMessageDTO> getPageDTO(Page<ContactMessage> contactMessagePage){
        Page<ContactMessageDTO> dtoPage =contactMessagePage.map(new Function<ContactMessage,ContactMessageDTO>() {
            @Override
            public ContactMessageDTO apply(ContactMessage contactMessage) {
                return contactMessageMapper.contactMessageToDTO(contactMessage);
            }
        });
        return dtoPage;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactMessageDTO> getMessageWithPath(@PathVariable("id") Long id){
        ContactMessage contactMessage = contactMessageService.getContactMessage(id);
        ContactMessageDTO contactMessageDTO = contactMessageMapper.contactMessageToDTO(contactMessage);
        return ResponseEntity.ok(contactMessageDTO);
    }

    @GetMapping("/request")
    public ResponseEntity<ContactMessageDTO> getMessageWithRequestParam(@RequestParam("id") Long id){
        ContactMessage contactMessage = contactMessageService.getContactMessage(id);
        ContactMessageDTO contactMessageDTO = contactMessageMapper.contactMessageToDTO(contactMessage);
        return ResponseEntity.ok(contactMessageDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SFResponse> deleteContactMessage(@PathVariable Long id){
        contactMessageService.deleteContactMessage(id);

        SFResponse SFresponse = new SFResponse(ResponseMessage.CONTACTMESSAGE_DELETE_RESPONSE_MESSAGE,true);

        return ResponseEntity.ok(SFresponse);

    }

    @PutMapping("/{id}")
    public ResponseEntity<SFResponse> updateContactMessage(@PathVariable Long id, @Valid @RequestBody ContactMessageRequest contactMessageRequest){

        ContactMessage contactMessage=contactMessageMapper.contactMessageRequestToContactMessage(contactMessageRequest);

        contactMessageService.updateContactMessage(id,contactMessage);

        SFResponse SFresponse = new SFResponse(ResponseMessage.CONTACTMESSAGE_UPDATE_RESPONSE_MESSAGE,true);

        return ResponseEntity.ok(SFresponse);
    }



}
