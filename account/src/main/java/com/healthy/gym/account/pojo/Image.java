package com.healthy.gym.account.pojo;

import org.bson.types.Binary;

import java.util.Objects;

public class Image {

    private Binary data;
    private String format;

    public Image() {
    }

    public Image(byte[] data, String format) {
        this.data = new Binary(data);
        this.format = format;
    }

    public Image(Binary data, String format) {
        this.data = data;
        this.format = format;
    }

    public Binary getData() {
        return data;
    }

    public void setData(Binary data) {
        this.data = data;
    }

    public void setDataBytes(byte[] data) {
        this.data = new Binary(data);
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
        Image image = (Image) o;
        return Objects.equals(data, image.data) && Objects.equals(format, image.format);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, format);
    }

    @Override
    public String toString() {
        return "Image{" +
                "data=" + data +
                ", format='" + format + '\'' +
                '}';
    }
}
