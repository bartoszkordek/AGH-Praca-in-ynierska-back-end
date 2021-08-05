package com.healthy.gym.gympass.configuration;

import java.util.Locale;

public class LocaleConverter {
    public static Locale convertEnumToLocale(TestCountry country) {
        if (country == TestCountry.POLAND) return new Locale("pl");
        return Locale.ENGLISH;
    }
}
