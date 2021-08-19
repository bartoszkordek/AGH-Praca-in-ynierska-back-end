package com.healthy.gym.account.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageDTO implements Serializable {

    private String data;
    private String format;

    public ImageDTO() {
    }

    public ImageDTO(String data, String format) {
        this.data = data;
        this.format = format;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageDTO imageDTO = (ImageDTO) o;
        return Objects.equals(data, imageDTO.data) && Objects.equals(format, imageDTO.format);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, format);
    }

    @Override
    public String toString() {
        return "ImageDTO{" +
                "data='" + data + '\'' +
                ", format='" + format + '\'' +
                '}';
    }
}
