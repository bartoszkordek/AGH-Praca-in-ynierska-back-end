package com.healthy.gym.equipment.model.response;

import com.healthy.gym.equipment.dto.EquipmentDTO;

import java.util.Objects;

public class EquipmentDTOResponse extends AbstractResponse {

    private EquipmentDTO equipment;

    public EquipmentDTOResponse(String message, EquipmentDTO equipment) {
        super(message);
        this.equipment = equipment;
    }

    public EquipmentDTOResponse() {

    }

    public EquipmentDTO getEquipment() {
        return equipment;
    }

    public void setEquipment(EquipmentDTO equipment) {
        this.equipment = equipment;
    }

    @Override
    public String toString() {
        return "EquipmentDTOResponse{" +
                "equipment=" + equipment +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EquipmentDTOResponse that = (EquipmentDTOResponse) o;
        return Objects.equals(equipment, that.equipment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), equipment);
    }
}
