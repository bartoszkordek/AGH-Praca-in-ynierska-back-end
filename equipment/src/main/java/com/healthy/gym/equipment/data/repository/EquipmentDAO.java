package com.healthy.gym.equipment.data.repository;

import com.healthy.gym.equipment.data.document.EquipmentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EquipmentDAO extends MongoRepository<EquipmentDocument, String> {

    boolean existsByTitle(String title);

    EquipmentDocument findByEquipmentId(String equipmentId);

    EquipmentDocument findByTitle(String equipmentTitle);

    void deleteByEquipmentId(String equipmentId);
}
