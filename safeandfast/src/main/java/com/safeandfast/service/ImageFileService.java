package com.safeandfast.service;

import com.safeandfast.domain.ImageData;
import com.safeandfast.domain.ImageFile;
import com.safeandfast.dto.ImageFileDTO;
import com.safeandfast.exception.ResourceNotFoundException;
import com.safeandfast.exception.message.ErrorMessage;
import com.safeandfast.repository.ImageFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ImageFileService {

    @Autowired
    private ImageFileRepository imageFileRepository;

    public String saveImage(MultipartFile file){

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        ImageFile imageFile=null;

        try {
            ImageData imData = new ImageData(file.getBytes());
            imageFile= new ImageFile(fileName,file.getContentType(),imData);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        imageFileRepository.save(imageFile);

        return imageFile.getId();
    }

    public ImageFile getImageById(String imageId){
     ImageFile imageFile= imageFileRepository.findById(imageId).orElseThrow(()-> new ResourceNotFoundException(String.format(ErrorMessage.IMAGE_NOT_FOUND_MESSAGE, imageId)));
     return imageFile;
    }

    public List<ImageFileDTO> getAllImages(){
        List<ImageFile> imageFiles= imageFileRepository.findAll();

        //http://localhost:8080/files/download/abc-ffdd
         List<ImageFileDTO> imageFileDTOS=imageFiles.stream().map(imageFile -> {
        String imageUri=ServletUriComponentsBuilder.fromCurrentContextPath().path("/files/download/").path(imageFile.getId()).toUriString();
            return new ImageFileDTO(imageFile.getName(),imageUri, imageFile.getType(),imageFile.getLength());
        }).collect(Collectors.toList());
        return imageFileDTOS;
    }

    public void removeById(String id) {
        ImageFile imfile=getImageById(id);
        imageFileRepository.delete(imfile);
    }

    public ImageFile findImageById(String id) {
        return imageFileRepository.findImageById(id).orElseThrow(() -> new
                ResourceNotFoundException(String.format(ErrorMessage.IMAGE_NOT_FOUND_MESSAGE, id)));
    }

}
