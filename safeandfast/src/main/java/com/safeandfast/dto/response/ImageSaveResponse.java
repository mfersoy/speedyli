package com.safeandfast.dto.response;

public class ImageSaveResponse extends SFResponse{

    private String imageId;

    public ImageSaveResponse(String imageId, String message, boolean success) {
        super(message,success);
        this.imageId= imageId;
    }
}
