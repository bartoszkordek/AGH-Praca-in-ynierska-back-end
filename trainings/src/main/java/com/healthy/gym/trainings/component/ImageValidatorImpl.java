package com.healthy.gym.trainings.component;

import org.apache.http.entity.ContentType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.UnsupportedDataTypeException;

@Component
public class ImageValidatorImpl implements ImageValidator {

    @Override
    public boolean isFileSupported(MultipartFile multipartFile) throws UnsupportedDataTypeException {
        if (multipartFile == null) throw new IllegalStateException();

        String contentType = multipartFile.getContentType();

        assert contentType != null;
        if (!contentType.equals(ContentType.IMAGE_JPEG.getMimeType())
                && !contentType.equals(ContentType.IMAGE_PNG.getMimeType()))
            throw new UnsupportedDataTypeException();
        return true;
    }
}
