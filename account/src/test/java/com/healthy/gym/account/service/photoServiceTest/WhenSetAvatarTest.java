package com.healthy.gym.account.service.photoServiceTest;

import com.healthy.gym.account.data.document.PhotoDocument;
import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.data.repository.PhotoDAO;
import com.healthy.gym.account.data.repository.UserDAO;
import com.healthy.gym.account.exception.PhotoSavingException;
import com.healthy.gym.account.pojo.Image;
import com.healthy.gym.account.service.PhotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class WhenSetAvatarTest {

    @Autowired
    private PhotoService photoService;

    @MockBean
    private PhotoDAO photoDAO;

    @MockBean
    private UserDAO userDAO;

    private String userId;
    private MockMultipartFile multipartFile;
    private PhotoDocument savedDocument;
    private PhotoDocument currentPhotoDocument;

    @BeforeEach
    void setUp() throws IOException {
        userId = UUID.randomUUID().toString();
        multipartFile = new MockMultipartFile(
                "avatar",
                "hello.png",
                MediaType.IMAGE_PNG_VALUE,
                "data".getBytes(StandardCharsets.UTF_8)
        );
        currentPhotoDocument = new PhotoDocument(
                userId,
                "currentTitle",
                new Image(
                        multipartFile.getBytes(),
                        MediaType.IMAGE_JPEG_VALUE
                )
        );
        savedDocument = new PhotoDocument(
                userId,
                multipartFile.getName(),
                new Image(
                        multipartFile.getBytes(),
                        multipartFile.getContentType()
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenProvidedUserIdIsInvalidOrDoestNotExist() {
        when(userDAO.findByUserId(userId)).thenReturn(null);
        assertThatThrownBy(
                () -> photoService.setAvatar(userId, multipartFile)
        ).isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenErrorOccursWhileSavingPhoto() {
        when(userDAO.findByUserId(userId)).thenReturn(new UserDocument());
        when(photoDAO.findByUserId(userId)).thenReturn(null);
        savedDocument.setTitle("differentTitle");
        when(photoDAO.save(any())).thenReturn(savedDocument);
        assertThatThrownBy(
                () -> photoService.setAvatar(userId, multipartFile)
        ).isInstanceOf(PhotoSavingException.class);
    }

    @Test
    void shouldProperlyUpdateAvatar() throws PhotoSavingException, IOException {
        when(userDAO.findByUserId(userId)).thenReturn(new UserDocument());
        when(photoDAO.findByUserId(userId)).thenReturn(currentPhotoDocument);
        when(photoDAO.save(any())).thenReturn(savedDocument);

        PhotoDocument avatar = photoService.setAvatar(userId, multipartFile);

        assertThat(avatar.getImage().getData().getData()).isEqualTo(multipartFile.getBytes());
        assertThat(avatar.getImage().getFormat()).isEqualTo(MediaType.IMAGE_PNG_VALUE);
        assertThat(avatar.getUserId()).isEqualTo(userId);
    }

    @Test
    void shouldProperlySetAvatar() throws PhotoSavingException, IOException {
        when(userDAO.findByUserId(userId)).thenReturn(new UserDocument());
        when(photoDAO.findByUserId(userId)).thenReturn(null);
        when(photoDAO.save(any())).thenReturn(savedDocument);

        PhotoDocument avatar = photoService.setAvatar(userId, multipartFile);

        assertThat(avatar.getImage().getData().getData()).isEqualTo(multipartFile.getBytes());
        assertThat(avatar.getImage().getFormat()).isEqualTo(MediaType.IMAGE_PNG_VALUE);
        assertThat(avatar.getUserId()).isEqualTo(userId);
    }
}
