package com.healthy.gym.auth.data.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "resetPasswordTokens")
public class ResetPasswordTokenDocument extends AbstractTokenDocument {
    private static final int EXPIRATION_IN_HOURS = 2;

    @Id
    private String id;

    public ResetPasswordTokenDocument() {
    }

    public ResetPasswordTokenDocument(String token, UserDocument userDocument) {
        super(token, userDocument, EXPIRATION_IN_HOURS);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ResetPasswordTokenDocument that = (ResetPasswordTokenDocument) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }

    @Override
    public String toString() {
        return "ResetPasswordTokenDocument{" +
                "id='" + id + '\'' +
                "} " + super.toString();
    }
}
