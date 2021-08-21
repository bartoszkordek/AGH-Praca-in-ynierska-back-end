package com.healthy.gym.account.service;

import com.healthy.gym.account.component.AvatarUrlCreator;
import com.healthy.gym.account.data.document.PhotoDocument;
import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.data.repository.PhotoDAO;
import com.healthy.gym.account.data.repository.UserDAO;
import com.healthy.gym.account.exception.PhotoSavingException;
import com.healthy.gym.account.exception.UserAvatarNotFoundException;
import com.healthy.gym.account.pojo.Image;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PhotoServiceImpl implements PhotoService {
    private final PhotoDAO photoDAO;
    private final UserDAO userDAO;
    private final AvatarUrlCreator avatarUrlCreator;

    @Autowired
    public PhotoServiceImpl(
            PhotoDAO photoDAO,
            UserDAO userDAO,
            AvatarUrlCreator avatarUrlCreator
    ) {
        this.photoDAO = photoDAO;
        this.userDAO = userDAO;
        this.avatarUrlCreator = avatarUrlCreator;
    }

    @Override
    public byte[] getAvatar(String userId) throws UserAvatarNotFoundException {
        PhotoDocument photoDocument = photoDAO.findByUserId(userId);
        if (photoDocument == null) throw new UserAvatarNotFoundException();

        Image image = photoDocument.getImage();
        Binary binary = image.getData();
        return binary.getData();
    }

    @Override
    public PhotoDocument removeAvatar(String userId) throws UserAvatarNotFoundException {
        PhotoDocument photoDocument = photoDAO.findByUserId(userId);
        if (photoDocument == null) throw new UserAvatarNotFoundException();

        UserDocument userDocument = userDAO.findByUserId(userId);
        if (userDocument == null) throw new UsernameNotFoundException("No user found id:" + userId);

        photoDAO.delete(photoDocument);
        userDocument.setAvatarUrl(null);
        userDAO.save(userDocument);
        return photoDAO.findPhotoDocumentById(photoDocument.getId());
    }

    @Override
    public String setAvatar(String userId, MultipartFile multipartFile)
            throws PhotoSavingException, IOException {

        UserDocument userDocument = userDAO.findByUserId(userId);
        if (userDocument == null) throw new UsernameNotFoundException("No user with provided id " + userId);

        Image image = new Image(
                multipartFile.getBytes(),
                multipartFile.getContentType()
        );
        String imageTitle = multipartFile.getName();

        PhotoDocument photoUpdated = setOrUpdateAvatar(userId, imageTitle, image);
        checkIfSavedCorrectly(photoUpdated, image, imageTitle);

        String avatarUrl = avatarUrlCreator.createAvatarUrl(userId);
        String version = DigestUtils.md5DigestAsHex(multipartFile.getBytes());
        userDocument.setAvatarUrl(avatarUrl + "/" + version);
        UserDocument updatedUser = userDAO.save(userDocument);

        return updatedUser.getAvatarUrl();
    }

    @Override
    public String getAvatarUrl(String userId) {
        UserDocument userDocument = userDAO.findByUserId(userId);
        if (userDocument == null) throw new UsernameNotFoundException("No user with provided id " + userId);
        return userDocument.getAvatarUrl();
    }

    private PhotoDocument setOrUpdateAvatar(
            String userId,
            String title,
            Image image
    ) {
        PhotoDocument photoUpdated;
        PhotoDocument actualPhoto = photoDAO.findByUserId(userId);

        if (actualPhoto != null) {
            actualPhoto.setTitle(title);
            actualPhoto.setImage(image);
            photoUpdated = photoDAO.save(actualPhoto);
        } else {
            PhotoDocument photoToSave = new PhotoDocument(userId, title, image);
            photoUpdated = photoDAO.save(photoToSave);
        }

        return photoUpdated;
    }

    private void checkIfSavedCorrectly(PhotoDocument photoUpdated, Image imageToSave, String imageTitleToSave)
            throws PhotoSavingException {
        if (!photoUpdated.getImage().equals(imageToSave) || !photoUpdated.getTitle().equals(imageTitleToSave))
            throw new PhotoSavingException();
    }
}
