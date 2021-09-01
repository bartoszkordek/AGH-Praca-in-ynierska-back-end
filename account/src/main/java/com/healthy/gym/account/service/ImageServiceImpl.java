package com.healthy.gym.account.service;

import com.healthy.gym.account.data.document.ImageDocument;
import com.healthy.gym.account.data.repository.ImageDAO;
import com.healthy.gym.account.exception.ImageNotFoundException;
import org.bson.types.Binary;
import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl implements ImageService{

    private final ImageDAO imageDAO;

    public ImageServiceImpl(ImageDAO imageDAO) {
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
