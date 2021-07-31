package com.healthy.gym.trainings.data.document;

import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import static com.healthy.gym.trainings.utils.Time24HoursValidator.validate;

@Document(collection = "IndividualTrainings")
public class IndividualTrainings {

    @Id
    private String id;
    private String clientId;
    private String trainerId;
    private String date;
    private String startTime;
    private String endTime;
    private int hallNo;
    private String remarks;
    private boolean accepted;
    private boolean declined;

    public IndividualTrainings() {

    }

    public IndividualTrainings(
            String clientId,
            String trainerId,
            String date,
            String startTime,
            String endTime,
            int hallNo,
            String remarks,
            boolean accepted,
            boolean declined
    ) throws InvalidHourException {

        if (!validate(startTime)) throw new InvalidHourException("Wrong start time");
        if (!validate(endTime)) throw new InvalidHourException("Wrong end time");

        this.clientId = clientId;
        this.trainerId = trainerId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.hallNo = hallNo;
        this.remarks = remarks;
        this.accepted = accepted;
        this.declined = declined;
    }

    @Override
    public String toString() {
        return "IndividualTrainings{" +
                "id='" + id + '\'' +
                ", clientId='" + clientId + '\'' +
                ", trainerId='" + trainerId + '\'' +
                ", date='" + date + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", hallNo=" + hallNo +
                ", remarks='" + remarks + '\'' +
                ", accepted=" + accepted +
                ", declined=" + declined +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(String trainerId) {
        this.trainerId = trainerId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) throws InvalidHourException {
        if (!validate(startTime)) throw new InvalidHourException("Wrong start time");
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) throws InvalidHourException {
        if (!validate(endTime)) throw new InvalidHourException("Wrong end time");
        this.endTime = endTime;
    }

    public int getHallNo() {
        return hallNo;
    }

    public void setHallNo(int hallNo) {
        this.hallNo = hallNo;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isDeclined() {
        return declined;
    }

    public void setDeclined(boolean declined) {
        this.declined = declined;
    }
}
