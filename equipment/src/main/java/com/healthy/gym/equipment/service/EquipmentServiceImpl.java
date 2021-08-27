package com.healthy.gym.equipment.service;

import com.healthy.gym.equipment.dto.EquipmentDTO;
import com.healthy.gym.equipment.model.request.EquipmentRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class EquipmentServiceImpl implements EquipmentService {
    @Override
    public List<EquipmentDTO> getEquipments() {
        return null;
    }

    @Override
    public EquipmentDTO createEquipment(EquipmentRequest equipmentRequest, MultipartFile multipartFile) {
        return null;
    }
}
