package com.healthy.gym.account.service;

import com.healthy.gym.account.data.document.PhotoDocument;
import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.data.repository.PhotoDAO;
import com.healthy.gym.account.data.repository.UserDAO;
import com.healthy.gym.account.exception.PhotoSavingException;
import com.healthy.gym.account.exception.UserAvatarNotFoundException;
import com.healthy.gym.account.shared.PhotoDTO;
import org.bson.types.Binary;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
    public PhotoDTO getAvatar(String userId) throws UserAvatarNotFoundException {
        PhotoDocument photoDocument = photoDAO.findByUserId(userId);
        if (photoDocument == null) throw new UserAvatarNotFoundException();

        PhotoDTO returnPhoto = modelMapper.map(photoDocument, PhotoDTO.class);
        returnPhoto.setImage(photoDocument.getImage().getData());

        return returnPhoto;
    }

    @Override
    public PhotoDTO setAvatar(String userId, PhotoDTO avatar) throws PhotoSavingException {
        UserDocument userDocument = userDAO.findByUserId(userId);
        if (userDocument == null) throw new UsernameNotFoundException("No user with provided id " + userId);

        PhotoDocument photoSaved = savePhoto(userId, avatar);
        PhotoDTO photoDTOSaved = modelMapper.map(photoSaved, PhotoDTO.class);
        photoDTOSaved.setImage(photoSaved.getImage().getData());

        if (!photoDTOSaved.equals(avatar)) throw new PhotoSavingException();

        return photoDTOSaved;
    }

    private PhotoDocument savePhoto(String userId, PhotoDTO avatar) {
        byte[] data = avatar.getImage();
        String title = avatar.getTitle();
        PhotoDocument photoToSave = new PhotoDocument(userId, title, new Binary(data));
        return photoDAO.save(photoToSave);
    }
}
