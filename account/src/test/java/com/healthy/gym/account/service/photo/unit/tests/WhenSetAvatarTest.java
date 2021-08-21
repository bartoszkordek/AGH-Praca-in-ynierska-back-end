package com.healthy.gym.account.service.photo.unit.tests;

import com.healthy.gym.account.component.AvatarUrlCreator;
import com.healthy.gym.account.data.document.PhotoDocument;
import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.data.repository.PhotoDAO;
import com.healthy.gym.account.data.repository.UserDAO;
import com.healthy.gym.account.exception.PhotoSavingException;
import com.healthy.gym.account.pojo.Image;
import com.healthy.gym.account.service.PhotoService;
import com.healthy.gym.account.service.PhotoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WhenSetAvatarTest {

    private PhotoService photoService;
    private PhotoDAO photoDAO;
    private UserDAO userDAO;
    private AvatarUrlCreator avatarUrlCreator;

    private String userId;
    private MockMultipartFile multipartFile;
    private PhotoDocument savedDocument;
    private PhotoDocument currentPhotoDocument;

    @BeforeEach
    void setUp() throws IOException {
        photoDAO = mock(PhotoDAO.class);
        userDAO = mock(UserDAO.class);
        avatarUrlCreator = mock(AvatarUrlCreator.class);
        photoService = new PhotoServiceImpl(photoDAO, userDAO, avatarUrlCreator);


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
        when(avatarUrlCreator.createAvatarUrl(anyString()))
                .thenReturn("http://localhost:8020/account/photos/" + userId);
        var user = new UserDocument();
        String digest = DigestUtils.md5DigestAsHex(savedDocument.getImage().getData().getData());
        user.setAvatarUrl("http://localhost:8020/account/photos/" + userId + "/version/" + digest);
        when(userDAO.save(any())).thenReturn(user);

        String avatar = photoService.setAvatar(userId, multipartFile);
        assertThat(avatar).startsWith("http://localhost:8020/account/photos/" + userId + "/version/");
    }
}
