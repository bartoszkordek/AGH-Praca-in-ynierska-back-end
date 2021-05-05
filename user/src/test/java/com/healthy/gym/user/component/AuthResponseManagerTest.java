package com.healthy.gym.user.component;

import com.healthy.gym.user.configuration.tests.TestCountry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static com.healthy.gym.user.configuration.tests.LocaleConverter.convertEnumToLocale;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AuthResponseManagerTest {

    @Autowired
    private AuthResponseManager authResponseManager;

    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        response = new MockHttpServletResponse();
    }

    @Test
    void shouldResponseWithProperMessageWhenTokenExpired() {
        // TODO write tests
        assertThat(true).isTrue();
    }

    @Test
    void shouldResponseWithProperMessageWhenUnsuccessfulLogout() {
        // TODO write tests
        assertThat(true).isTrue();
    }

    @Nested
    class WhenSuccessfulLogout {

        @Nested
        class WithLocaleInfoUseless {
            @BeforeEach
            void setUp() throws IOException {
                authResponseManager.handleSuccessfulLogout(response);
            }

            @Test
            void shouldHaveStatus200() {
                assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            }

            @Test
            void shouldHaveCharacterEncodingUTF8() {
                assertThat(response.getCharacterEncoding()).isEqualTo(StandardCharsets.UTF_8.toString());
            }

            @Test
            void shouldHaveJSONResponse() {
                assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
            }
        }

        @Nested
        class WithLocaleInfoUseful {

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldHaveProperLocale(TestCountry testCountry) throws IOException {
                Locale testedLocale = convertEnumToLocale(testCountry);
                LocaleContextHolder.setLocale(testedLocale);

                authResponseManager.handleSuccessfulLogout(response);
                assertThat(response.getLocale()).isEqualTo(testedLocale);
            }
        }
    }
}