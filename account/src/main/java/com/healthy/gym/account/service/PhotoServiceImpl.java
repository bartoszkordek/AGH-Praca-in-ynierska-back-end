package com.healthy.gym.account.service;

import com.healthy.gym.account.data.document.PhotoDocument;
import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.data.repository.PhotoDAO;
import com.healthy.gym.account.data.repository.UserDAO;
import com.healthy.gym.account.exception.PhotoSavingException;
import com.healthy.gym.account.exception.UserAvatarNotFoundException;
import com.healthy.gym.account.pojo.Image;
import com.healthy.gym.account.shared.ImageDTO;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
public class PhotoServiceImpl implements PhotoService {
    private final PhotoDAO photoDAO;
    private final UserDAO userDAO;

    @Autowired
    public PhotoServiceImpl(PhotoDAO photoDAO, UserDAO userDAO) {
        this.photoDAO = photoDAO;
        this.userDAO = userDAO;
    }

    @Override
    public ImageDTO getAvatar(String userId) throws UserAvatarNotFoundException {
        PhotoDocument photoDocument = photoDAO.findByUserId(userId);
        if (photoDocument == null) throw new UserAvatarNotFoundException();

        String data = getDataEncodeBase64(photoDocument);
        String format = photoDocument.getImage().getFormat();

        return new ImageDTO(data, format);
    }

    private String getDataEncodeBase64(PhotoDocument photoDocument) {
        Base64.Encoder encoder = Base64.getEncoder();
        Image image = photoDocument.getImage();
        Binary binary = image.getData();
        byte[] data = binary.getData();
        return encoder.encodeToString(data);
    }

    @Override
    public ImageDTO setAvatar(String userId, MultipartFile multipartFile) throws PhotoSavingException, IOException {
        UserDocument userDocument = userDAO.findByUserId(userId);
        if (userDocument == null) throw new UsernameNotFoundException("No user with provided id " + userId);

        Image image = new Image(
                multipartFile.getBytes(),
                multipartFile.getContentType()
        );
        String imageTitle = multipartFile.getName();

        PhotoDocument photoUpdated = setOrUpdateAvatar(userId, imageTitle, image);
        checkIfSavedCorrectly(photoUpdated, image, imageTitle);

        String data = getDataEncodeBase64(photoUpdated);
        String format = photoUpdated.getImage().getFormat();

        return new ImageDTO(data, format);
    }

    private PhotoDocument setOrUpdateAvatar(
            String userId,
            String title,
            Image image
    ) {
        PhotoDocument photoUpdated;
        PhotoDocument actualPhoto = getCurrentAvatar(userId);

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

    private PhotoDocument getCurrentAvatar(String userId) {
        return photoDAO.findByUserId(userId);
    }

    private void checkIfSavedCorrectly(PhotoDocument photoUpdated, Image imageToSave, String imageTitleToSave)
            throws PhotoSavingException {
        System.out.println(photoUpdated);
        System.out.println(imageToSave);
        System.out.println(imageTitleToSave);
        if (!photoUpdated.getImage().equals(imageToSave) || !photoUpdated.getTitle().equals(imageTitleToSave))
            throw new PhotoSavingException();
    }
}
