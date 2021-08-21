package com.healthy.gym.account.service.photoServiceTest;

import com.healthy.gym.account.data.document.PhotoDocument;
import com.healthy.gym.account.data.repository.PhotoDAO;
import com.healthy.gym.account.exception.UserAvatarNotFoundException;
import com.healthy.gym.account.pojo.Image;
import com.healthy.gym.account.service.PhotoService;
import com.healthy.gym.account.service.PhotoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WhenRemoveAvatarTest {

    private PhotoDAO photoDAO;
    private PhotoService photoService;

    private String userId;
    private PhotoDocument photoDocument;

    @BeforeEach
    void setUp() {
        photoDAO = mock(PhotoDAO.class);
        photoService = new PhotoServiceImpl(photoDAO, null, null);

        userId = UUID.randomUUID().toString();
        Image image = new Image(
                "testData".getBytes(StandardCharsets.UTF_8),
                MediaType.IMAGE_JPEG_VALUE
        );
        photoDocument = new PhotoDocument(userId, "testTitle", image);
    }

    @Test
    void shouldThrowExceptionWhenProvidedUserIdIsInvalidOrAvatarDoestNotExist() {
        when(photoDAO.findByUserId(userId)).thenReturn(null);
        assertThatThrownBy(() -> photoService.removeAvatar(userId))
                .isInstanceOf(UserAvatarNotFoundException.class);
    }

    @Test
    void shouldReturnAvatarWhenProvidedUserIdValid() throws UserAvatarNotFoundException {
        when(photoDAO.findByUserId(userId)).thenReturn(photoDocument);
        when(photoDAO.findPhotoDocumentById(any())).thenReturn(null);

        PhotoDocument photoDocument = photoService.removeAvatar(userId);

        assertThat(photoDocument).isNull();
    }
}
