package com.healthy.gym.account.component;

public interface Translator {

    String toLocale(String message, Object[] args);

    String toLocale(String message);
}
