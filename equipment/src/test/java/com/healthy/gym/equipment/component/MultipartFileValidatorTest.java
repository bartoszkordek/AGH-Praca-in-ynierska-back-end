package com.healthy.gym.equipment.component;

import com.healthy.gym.equipment.configuration.TestCountry;
import com.healthy.gym.equipment.exception.MultipartBodyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.healthy.gym.equipment.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.equipment.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles(value = "test")
class MultipartFileValidatorTest {

    @Autowired
    private MultipartFileValidator validator;

    private TestRequest testRequest;

    @BeforeEach
    void setUp() {
        testRequest = new TestRequest();
    }

    @Test
    void shouldNotThrowExceptionWhenAllFieldAreValid() throws MultipartBodyException {
        testRequest.setDescription("test Description");
        testRequest.setName("Test name");

        assertThat(validator.validateBody(testRequest)).isTrue();
    }

    @Nested
    class ShouldThrowExceptionWhenAtLeastOneFieldIsInvalid {

        @BeforeEach
        void setUp() {
            testRequest.setName("s");
            testRequest.setDescription("a");
        }

        @Test
        void shouldThrowProperException() {
            assertThatThrownBy(() -> validator.validateBody(testRequest))
                    .isInstanceOf(MultipartBodyException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowExceptionWhenAtLeastOneFieldIsInvalid(TestCountry country) {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            Map<String, String> expectedErrors = new HashMap<>();
            expectedErrors.put("name", messages.get("field.required"));
            expectedErrors.put("description", messages.get("field.required"));

            LocaleContextHolder.setLocale(testedLocale);

            try {
                validator.validateBody(testRequest);
            } catch (MultipartBodyException exception) {
                assertThat(exception.getErrorMap())
                        .isNotEmpty()
                        .isEqualTo(expectedErrors);
            }
        }
    }

    private static class TestRequest {
        @NotNull(message = "{field.required}")
        @Size(min = 2, max = 1000, message = "{field.required}")
        private String name;

        @NotNull(message = "{field.required}")
        @Size(min = 2, max = 1000, message = "{field.required}")
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}