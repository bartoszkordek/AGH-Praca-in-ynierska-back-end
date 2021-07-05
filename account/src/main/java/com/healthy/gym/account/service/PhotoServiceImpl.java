package com.healthy.gym.account.service;

import com.healthy.gym.account.data.document.PhotoDocument;
import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.data.repository.PhotoDAO;
import com.healthy.gym.account.data.repository.UserDAO;
import com.healthy.gym.account.exception.PhotoSavingException;
import com.healthy.gym.account.exception.UserAvatarNotFoundException;
import com.healthy.gym.account.pojo.Image;
import com.healthy.gym.account.shared.ImageDTO;
import com.healthy.gym.account.shared.PhotoDTO;
import org.bson.types.Binary;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
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
    private final ModelMapper modelMapper;

    @Autowired
    public PhotoServiceImpl(PhotoDAO photoDAO, UserDAO userDAO) {
        this.photoDAO = photoDAO;
        this.userDAO = userDAO;
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
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
        PhotoDTO avatar = new PhotoDTO(
                userId,
                multipartFile.getOriginalFilename(),
                new Image(
                        multipartFile.getBytes(),
                        multipartFile.getContentType()
                )
        );

        PhotoDocument photoSaved = savePhoto(userId, avatar);
        PhotoDTO photoDTOSaved = modelMapper.map(photoSaved, PhotoDTO.class);

        if (!photoDTOSaved.equals(avatar)) throw new PhotoSavingException();

        //return photoDTOSaved;
        return new ImageDTO();
    }

    private PhotoDocument savePhoto(String userId, PhotoDTO avatar) {
        Image image = avatar.getImage();
        String title = avatar.getTitle();

        PhotoDocument actualPhoto = photoDAO.findByUserId(userId);

        if (actualPhoto != null) {
            actualPhoto.setTitle(title);
            actualPhoto.setImage(image);
            return photoDAO.save(actualPhoto);
        }

        PhotoDocument photoToSave = new PhotoDocument(userId, title, image);
        return photoDAO.save(photoToSave);
    }
}
