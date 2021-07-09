package com.healthy.gym.auth.data.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "usersPrivacy")
public class UserPrivacyDocument {

    @Id
    private String id;
    private boolean regulationsAccepted;
    private boolean allowShowingTrainingsParticipation;
    private boolean allowShowingUserStatistics;
    private boolean allowShowingAvatar;
    @DBRef
    private UserDocument userDocument;

    public UserPrivacyDocument() {
    }

    public UserPrivacyDocument(
            boolean regulationsAccepted,
            boolean allowShowingTrainingsParticipation,
            boolean allowShowingUserStatistics,
            boolean allowShowingAvatar, UserDocument userDocument
    ) {
        this.regulationsAccepted = regulationsAccepted;
        this.allowShowingTrainingsParticipation = allowShowingTrainingsParticipation;
        this.allowShowingUserStatistics = allowShowingUserStatistics;
        this.allowShowingAvatar = allowShowingAvatar;
        this.userDocument = userDocument;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isRegulationsAccepted() {
        return regulationsAccepted;
    }

    public void setRegulationsAccepted(boolean regulationsAccepted) {
        this.regulationsAccepted = regulationsAccepted;
    }

    public boolean isAllowShowingTrainingsParticipation() {
        return allowShowingTrainingsParticipation;
    }

    public void setAllowShowingTrainingsParticipation(boolean allowShowingTrainingsParticipation) {
        this.allowShowingTrainingsParticipation = allowShowingTrainingsParticipation;
    }

    public boolean isAllowShowingUserStatistics() {
        return allowShowingUserStatistics;
    }

    public void setAllowShowingUserStatistics(boolean allowShowingUserStatistics) {
        this.allowShowingUserStatistics = allowShowingUserStatistics;
    }

    public boolean isAllowShowingAvatar() {
        return allowShowingAvatar;
    }

    public void setAllowShowingAvatar(boolean allowShowingAvatar) {
        this.allowShowingAvatar = allowShowingAvatar;
    }

    public UserDocument getUserDocument() {
        return userDocument;
    }

    public void setUserDocument(UserDocument userDocument) {
        this.userDocument = userDocument;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrivacyDocument that = (UserPrivacyDocument) o;
        return regulationsAccepted == that.regulationsAccepted
                && allowShowingTrainingsParticipation == that.allowShowingTrainingsParticipation
                && allowShowingUserStatistics == that.allowShowingUserStatistics
                && allowShowingAvatar == that.allowShowingAvatar
                && Objects.equals(id, that.id)
                && Objects.equals(userDocument, that.userDocument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                regulationsAccepted,
                allowShowingTrainingsParticipation,
                allowShowingUserStatistics,
                allowShowingAvatar,
                userDocument
        );
    }

    @Override
    public String toString() {
        return "UserPrivacyDocument{" +
                "id='" + id + '\'' +
                ", regulationsAccepted=" + regulationsAccepted +
                ", allowShowingTrainingsParticipation=" + allowShowingTrainingsParticipation +
                ", allowShowingUserStatistics=" + allowShowingUserStatistics +
                ", allowShowingAvatar=" + allowShowingAvatar +
                ", userDocument=" + userDocument +
                '}';
    }
}
