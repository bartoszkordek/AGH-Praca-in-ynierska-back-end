package com.healthy.gym.trainings.model.response;

import com.healthy.gym.trainings.shared.GroupTrainingDTO;
import com.healthy.gym.trainings.shared.GroupTrainingEnrollmentDTO;

import java.util.Map;

public class GroupTrainingEnrollmentResponse extends AbstractResponse{

    private GroupTrainingEnrollmentDTO enrollment;

    public GroupTrainingEnrollmentResponse() {
    }

    public GroupTrainingEnrollmentResponse(String message, GroupTrainingEnrollmentDTO enrollment) {
        super(message);
        this.enrollment = enrollment;
    }

    public GroupTrainingEnrollmentResponse(String message,
                                           Map<String, String> errors,
                                           GroupTrainingEnrollmentDTO enrollment) {
        super(message, errors);
        this.enrollment = enrollment;
    }

}
