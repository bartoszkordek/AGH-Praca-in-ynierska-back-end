package com.healthy.gym.equipment.service;

import com.healthy.gym.equipment.dto.EquipmentDTO;
import com.healthy.gym.equipment.exception.DuplicatedEquipmentTypeException;
import com.healthy.gym.equipment.exception.EquipmentNotFoundException;
import com.healthy.gym.equipment.model.request.EquipmentRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EquipmentService {

    List<EquipmentDTO> getEquipments() throws EquipmentNotFoundException;

    EquipmentDTO createEquipment(EquipmentRequest equipmentRequest, MultipartFile multipartFile)
        throws DuplicatedEquipmentTypeException;
}
