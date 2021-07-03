package com.healthy.gym.account.service;

import com.healthy.gym.account.data.document.PhotoDocument;
import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.data.repository.PhotoDAO;
import com.healthy.gym.account.data.repository.UserDAO;
import com.healthy.gym.account.exception.PhotoSavingException;
import com.healthy.gym.account.exception.UserAvatarNotFoundException;
import com.healthy.gym.account.pojo.Image;
import com.healthy.gym.account.shared.PhotoDTO;
import org.bson.types.Binary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
class PhotoServiceTest {

    private PhotoDTO photoDTO;
    private PhotoDocument photoDocument;
    private String userId;

    @Autowired
    private PhotoService photoService;

    @MockBean
    private PhotoDAO photoDAO;

    @MockBean
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        byte[] data = "sample data".getBytes(StandardCharsets.UTF_8);
        userId = UUID.randomUUID().toString();
        photoDTO = new PhotoDTO(userId, "Avatar", new Image(data, MediaType.IMAGE_JPEG_VALUE));
        photoDocument = new PhotoDocument(userId, "Avatar",
                new Image(new Binary(data), MediaType.IMAGE_JPEG_VALUE)
        );
    }

    @Nested
    class WhenGetAvatar {
        @Test
        void shouldThrowExceptionWhenAvatarNotFound() {
            when(photoDAO.findByUserId(userId)).thenReturn(null);
            assertThatThrownBy(
                    () -> photoService.getAvatar(userId)
            ).isInstanceOf(UserAvatarNotFoundException.class);
        }

        @Test
        void shouldReturnPhotoWhenFound() throws UserAvatarNotFoundException {
            when(photoDAO.findByUserId(userId)).thenReturn(photoDocument);
            PhotoDTO returnPhoto = photoService.getAvatar(userId);
            assertThat(returnPhoto).isEqualTo(photoDTO);
        }
    }

    @Nested
    class WhenSetAvatar {
        @Test
        void shouldThrowExceptionWhenUserNotFound() {
            when(userDAO.findByUserId(userId)).thenReturn(null);
            assertThatThrownBy(
                    () -> photoService.setAvatar(photoDTO)
            ).isInstanceOf(UsernameNotFoundException.class);
        }

        @Test
        void shouldThrowExceptionWhenErrorOccursWhileSavingPhoto() {
            when(userDAO.findByUserId(userId)).thenReturn(new UserDocument());
            PhotoDocument savedDocument = new PhotoDocument(
                    UUID.randomUUID().toString(),
                    "title",
                    new Image(
                            new Binary("data".getBytes(StandardCharsets.UTF_8)), MediaType.IMAGE_JPEG_VALUE
                    )
            );
            when(photoDAO.save(photoDocument)).thenReturn(savedDocument);
            assertThatThrownBy(
                    () -> photoService.setAvatar(photoDTO)
            ).isInstanceOf(PhotoSavingException.class);
        }

        @Test
        void shouldProperlySetAvatar() throws PhotoSavingException {
            when(userDAO.findByUserId(userId)).thenReturn(new UserDocument());
            byte[] data = "sample data".getBytes(StandardCharsets.UTF_8);
            PhotoDocument savedDocument = new PhotoDocument(
                    userId,
                    "Avatar",
                    new Image(new Binary(data), MediaType.IMAGE_JPEG_VALUE)
            );
            when(photoDAO.save(photoDocument)).thenReturn(savedDocument);
            PhotoDTO setAvatar = photoService.setAvatar(photoDTO);
            assertThat(setAvatar).isEqualTo(photoDTO);
        }
    }
}