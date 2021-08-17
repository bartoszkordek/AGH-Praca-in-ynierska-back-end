package com.healthy.gym.task.component;

public interface Translator {

    String toLocale(String message, Object[] args);

    String toLocale(String message);
}