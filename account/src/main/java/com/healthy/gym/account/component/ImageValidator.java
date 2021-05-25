package com.healthy.gym.account.component;

import org.springframework.web.multipart.MultipartFile;

import javax.activation.UnsupportedDataTypeException;

public interface ImageValidator {
    boolean isFileSupported(MultipartFile multipartFile) throws UnsupportedDataTypeException;
}
