package com.healthy.gym.equipment.service;

import com.healthy.gym.equipment.data.document.ImageDocument;
import com.healthy.gym.equipment.data.repository.ImageDAO;
import com.healthy.gym.equipment.exception.ImageNotFoundException;
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
