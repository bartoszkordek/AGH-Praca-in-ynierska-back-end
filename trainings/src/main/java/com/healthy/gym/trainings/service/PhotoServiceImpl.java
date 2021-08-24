package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.data.document.ImageDocument;
import com.healthy.gym.trainings.data.repository.ImageDAO;
import com.healthy.gym.trainings.exception.notfound.ImageNotFoundException;
import org.bson.types.Binary;
import org.springframework.stereotype.Service;

@Service
public class PhotoServiceImpl implements PhotoService {

    private final ImageDAO imageDAO;

    public PhotoServiceImpl(ImageDAO imageDAO) {
        this.imageDAO = imageDAO;
    }

    @Override
    public byte[] getImage(String imageId) throws ImageNotFoundException {
        ImageDocument imageDocument = imageDAO.findByImageId(imageId);
        if (imageDocument == null) throw new ImageNotFoundException();

        Binary binary = imageDocument.getImageData();
        return binary.getData();
    }
}
