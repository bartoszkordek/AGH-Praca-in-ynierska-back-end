package com.healthy.gym.trainings.controller.location.unit.tests;

import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.controller.LocationController;
import com.healthy.gym.trainings.service.LocationService;
import com.healthy.gym.trainings.dto.LocationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static com.healthy.gym.trainings.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.trainings.configuration.Messages.getMessagesAccordingToLocale;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationController.class)
@ActiveProfiles(value = "test")
class WhenGetLocationsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private LocationService locationService;

    private String managerToken;
    private String adminToken;
    private String userToken;
    private URI uri;

    @BeforeEach
    void setUp() throws URISyntaxException {
        String userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        String managerId = UUID.randomUUID().toString();
        managerToken = tokenFactory.getManagerToken(managerId);

        String adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);

        uri = new URI("/location");
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldReturnAllLocations(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON);

        String locationID1 = UUID.randomUUID().toString();
        String locationID2 = UUID.randomUUID().toString();

        List<LocationDTO> returnedList = List.of(
                new LocationDTO(
                        locationID1,
                        "Sala nr 1"
                ),
                new LocationDTO(
                        locationID2,
                        "Sala nr 2"
                )
        );

        when(locationService.getAllLocations()).thenReturn(returnedList);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").doesNotHaveJsonPath(),
                        jsonPath("$.[0].locationId").value(is(locationID1)),
                        jsonPath("$.[0].name").value(is("Sala nr 1")),
                        jsonPath("$.[1].locationId").value(is(locationID2)),
                        jsonPath("$.[1].name").value(is("Sala nr 2"))
                ));
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldReturnEmptyLocationList(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON);

        when(locationService.getAllLocations()).thenReturn(new ArrayList<>());

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").doesNotHaveJsonPath(),
                        jsonPath("$").isEmpty()
                ));
    }

    @Nested
    class ShouldRejectRequest {
        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserDoesNotHaveAdminOrManagerRole(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", userToken)
                    .contentType(MediaType.APPLICATION_JSON);

            String expectedMessage = messages.get("exception.access.denied");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value(is(expectedMessage)))
                    .andExpect(jsonPath("$.error").value(is("Forbidden")))
                    .andExpect(jsonPath("$.status").value(403))
                    .andExpect(jsonPath("$.timestamp").exists());
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserIsNotLogIn(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri)
                    .header("Accept-Language", testedLocale.toString());

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }
}
