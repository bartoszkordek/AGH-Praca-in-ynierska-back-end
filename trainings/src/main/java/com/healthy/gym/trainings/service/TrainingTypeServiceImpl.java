package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.data.document.ImageDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.repository.ImageDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.DuplicatedTrainingTypeException;
import com.healthy.gym.trainings.exception.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.other.TrainingTypeModel;
import com.healthy.gym.trainings.model.request.TrainingTypeRequest;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeDAO trainingTypeRepository;
    private final ImageDAO imageDAO;

    @Autowired
    public TrainingTypeServiceImpl(
            TrainingTypeDAO trainingTypeRepository,
            ImageDAO imageDAO
    ) {
        this.trainingTypeRepository = trainingTypeRepository;
        this.imageDAO = imageDAO;
    }

    @Override
    public TrainingTypeDocument createTrainingType(
            TrainingTypeRequest trainingTypeRequest,
            MultipartFile multipartFile
    ) throws DuplicatedTrainingTypeException {
        String name = trainingTypeRequest.getName();

        if (trainingTypeRepository.existsByName(name)) throw new DuplicatedTrainingTypeException();
        ImageDocument savedImageDocument = null;
        if (multipartFile != null) {
            try {
                ImageDocument imageDocument = new ImageDocument(
                        UUID.randomUUID().toString(),
                        new Binary(multipartFile.getBytes()),
                        multipartFile.getContentType()
                );
                savedImageDocument = imageDAO.save(imageDocument);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        TrainingTypeDocument trainingTypeDocument = new TrainingTypeDocument(
                UUID.randomUUID().toString(),
                name,
                trainingTypeRequest.getDescription(),
                getDuration(trainingTypeRequest),
                savedImageDocument
        );

        return trainingTypeRepository.save(trainingTypeDocument);
    }

    private LocalTime getDuration(TrainingTypeRequest trainingTypeRequest) {
        String duration = trainingTypeRequest.getDuration();
        return LocalTime.parse(duration, DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
    }

    @Override
    public List<TrainingTypeDocument> getAllTrainingTypes() throws TrainingTypeNotFoundException {
        List<TrainingTypeDocument> trainingTypes = trainingTypeRepository.findAll();
        if (trainingTypes.isEmpty()) throw new TrainingTypeNotFoundException();
        return trainingTypes;
    }

    @Override
    public TrainingTypeDocument getTrainingTypeById(String trainingTypeId) throws TrainingTypeNotFoundException {
        TrainingTypeDocument trainingTypeDocument = trainingTypeRepository.findByTrainingTypeId(trainingTypeId);
        if (trainingTypeDocument == null) throw new TrainingTypeNotFoundException();
        return trainingTypeDocument;
    }

    @Override
    public TrainingTypeDocument updateTrainingTypeById(
            String trainingId,
            TrainingTypeRequest trainingTypeRequest,
            MultipartFile multipartFile
    ) throws TrainingTypeNotFoundException, DuplicatedTrainingTypeException {
        TrainingTypeDocument trainingTypeDocumentFound = trainingTypeRepository.findByTrainingTypeId(trainingId);
        if (trainingTypeDocumentFound == null) throw new TrainingTypeNotFoundException();

        String updatedTrainingName = trainingTypeRequest.getName();
        if (!trainingTypeDocumentFound.getName().equals(updatedTrainingName)
                && trainingTypeRepository.existsByName(updatedTrainingName)) {
            throw new DuplicatedTrainingTypeException();
        }

        trainingTypeDocumentFound.setDescription(trainingTypeRequest.getDescription());
        trainingTypeDocumentFound.setDuration(getDuration(trainingTypeRequest));
        trainingTypeDocumentFound.setName(trainingTypeRequest.getName());
        if (multipartFile != null) {
            try {
                ImageDocument imageToUpdate;
                if (trainingTypeDocumentFound.getImageDocument() != null) {
                    imageToUpdate = trainingTypeDocumentFound.getImageDocument();
                    imageToUpdate.setImageData(new Binary(multipartFile.getBytes()));
                    imageToUpdate.setContentType(multipartFile.getContentType());

                } else {
                    imageToUpdate = new ImageDocument(
                            UUID.randomUUID().toString(),
                            new Binary(multipartFile.getBytes()),
                            multipartFile.getContentType()
                    );
                }
                ImageDocument savedImageDocument = imageDAO.save(imageToUpdate);
                trainingTypeDocumentFound.setImageDocument(savedImageDocument);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return trainingTypeRepository.save(trainingTypeDocumentFound);
    }

    @Override
    public TrainingTypeDocument removeTrainingTypeById(String trainingName) throws TrainingTypeNotFoundException {
        TrainingTypeDocument trainingTypeToRemove = trainingTypeRepository.findByTrainingTypeId(trainingName);
        if (trainingTypeToRemove == null) throw new TrainingTypeNotFoundException();
        trainingTypeRepository.deleteByTrainingTypeId(trainingName);
        return trainingTypeToRemove;
    }
}
