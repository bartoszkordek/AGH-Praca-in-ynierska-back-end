package com.healthy.gym.account.component;

import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import javax.activation.UnsupportedDataTypeException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ImageValidatorTest {

    private byte[] data;
    private String title;
    private String originalName;

    @Autowired
    private ImageValidator imageValidator;

    @BeforeEach
    void setUp() {
        title = "title";
        originalName = "originalName";
        data = "data".getBytes(StandardCharsets.UTF_8);
    }

    @Test
    void shouldJPEGImageBeValid() throws UnsupportedDataTypeException {
        MockMultipartFile multipartFile = new MockMultipartFile(
                title,
                originalName,
                ContentType.IMAGE_JPEG.getMimeType(),
                data
        );
        assertThat(imageValidator.isFileSupported(multipartFile)).isTrue();
    }

    @Test
    void shouldPNGImageBeValid() throws UnsupportedDataTypeException {
        MockMultipartFile multipartFile = new MockMultipartFile(
                title,
                originalName,
                ContentType.IMAGE_PNG.getMimeType(),
                data
        );
        assertThat(imageValidator.isFileSupported(multipartFile)).isTrue();
    }

    @Test
    void shouldAnotherFileContentTypeShouldNotBeValid() {
        MockMultipartFile multipartFile = new MockMultipartFile(
                title,
                originalName,
                "*/*",
                data
        );
        assertThatThrownBy(() -> imageValidator.isFileSupported(multipartFile))
                .isInstanceOf(UnsupportedDataTypeException.class);
    }
}