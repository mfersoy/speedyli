package com.safeandfast.controller;

import com.safeandfast.domain.ImageFile;
import com.safeandfast.dto.ImageFileDTO;
import com.safeandfast.dto.response.ImageSaveResponse;
import com.safeandfast.dto.response.ResponseMessage;
import com.safeandfast.dto.response.SFResponse;
import com.safeandfast.service.ImageFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/files")
public class ImageFileController {

    @Autowired
    private ImageFileService imageFileService;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ImageSaveResponse> uploadFile(@RequestParam("file") MultipartFile file){

        String imageId = imageFileService.saveImage(file);
        ImageSaveResponse response = new ImageSaveResponse(imageId, ResponseMessage.IMAGE_SAVED_RESPONSE_MESSAGE, true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String id){
        ImageFile imageFile = imageFileService.getImageById(id);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename="+imageFile.getName()).body(imageFile.getImageData().getData());
    }

    @GetMapping("/display/{id}")
    public ResponseEntity<byte[]> displayFile(@PathVariable String id){
        ImageFile imageFile = imageFileService.getImageById(id);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(imageFile.getImageData().getData(), header, HttpStatus.OK );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public  ResponseEntity<List<ImageFileDTO>> getAllImage(){
       List<ImageFileDTO> allImageDTO = imageFileService.getAllImages();
       return ResponseEntity.ok(allImageDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SFResponse> deleteImageFile(@PathVariable String id){

        imageFileService.removeById(id);
        SFResponse sfResponse = new SFResponse(ResponseMessage.IMAGE_DELETE_RESPONSE_MESSAGE,true);
        return ResponseEntity.ok(sfResponse);

    }


}
