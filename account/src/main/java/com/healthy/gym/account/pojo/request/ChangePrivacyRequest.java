package com.healthy.gym.account.pojo.request;

import java.util.Objects;

public class ChangePrivacyRequest {
    private boolean regulationsAccepted;
    private boolean allowShowingTrainingsParticipation;
    private boolean allowShowingUserStatistics;
    private boolean allowShowingAvatar;

    public ChangePrivacyRequest() {
    }

    public ChangePrivacyRequest(
            boolean regulationsAccepted,
            boolean allowShowingTrainingsParticipation,
            boolean allowShowingUserStatistics,
            boolean allowShowingAvatar
    ) {
        this.regulationsAccepted = regulationsAccepted;
        this.allowShowingTrainingsParticipation = allowShowingTrainingsParticipation;
        this.allowShowingUserStatistics = allowShowingUserStatistics;
        this.allowShowingAvatar = allowShowingAvatar;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChangePrivacyRequest that = (ChangePrivacyRequest) o;
        return regulationsAccepted == that.regulationsAccepted
                && allowShowingTrainingsParticipation == that.allowShowingTrainingsParticipation
                && allowShowingUserStatistics == that.allowShowingUserStatistics
                && allowShowingAvatar == that.allowShowingAvatar;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                regulationsAccepted,
                allowShowingTrainingsParticipation,
                allowShowingUserStatistics,
                allowShowingAvatar
        );
    }

    @Override
    public String toString() {
        return "ChangePrivacyRequest{" +
                "regulationsAccepted=" + regulationsAccepted +
                ", allowShowingTrainingsParticipation=" + allowShowingTrainingsParticipation +
                ", allowShowingUserStatistics=" + allowShowingUserStatistics +
                ", allowShowingAvatar=" + allowShowingAvatar +
                '}';
    }
}
