package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.repository.LocationDAO;
import com.healthy.gym.trainings.exception.duplicated.DuplicatedLocationNameException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.model.request.LocationRequest;
import com.healthy.gym.trainings.shared.LocationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class LocationServiceTest {

    @Autowired
    private LocationService locationService;

    @MockBean
    private LocationDAO locationDAO;

    @Nested
    class WhenCreateLocation {

        @Test
        void shouldThrowDuplicatedNameException() {
            when(locationDAO.findByName("Sala nr2"))
                    .thenReturn(new LocationDocument(null, "Sala nr2"));

            LocationRequest request = new LocationRequest();
            request.setName("Sala nr2");

            assertThatThrownBy(() ->
                    locationService.createLocation(request)
            ).isInstanceOf(DuplicatedLocationNameException.class);
        }

        @Test
        void shouldCreateLocation() throws DuplicatedLocationNameException {
            String locationId = UUID.randomUUID().toString();
            when(locationDAO.findByName("Sala nr2")).thenReturn(null);
            when(locationDAO.save(any())).thenReturn(
                    new LocationDocument(locationId, "Sala nr 2")
            );

            LocationRequest request = new LocationRequest();
            request.setName("Sala nr2");

            LocationDTO createdLocation = locationService.createLocation(request);

            assertThat(createdLocation.getName()).isEqualTo("Sala nr 2");
            assertThat(createdLocation.getLocationId()).isEqualTo(locationId);
        }
    }

    @Nested
    class WhenGetAllLocations {

        @Test
        void shouldReturnAllLocationList() {
            String location1 = UUID.randomUUID().toString();
            String location2 = UUID.randomUUID().toString();

            List<LocationDocument> locationDocumentList = List.of(
                    new LocationDocument(location1, "TestLocationName1"),
                    new LocationDocument(location2, "TestLocationName2")
            );

            when(locationDAO.findAll()).thenReturn(locationDocumentList);

            assertThat(locationService.getAllLocations())
                    .isNotNull()
                    .hasSize(2)
                    .isEqualTo(List.of(
                            new LocationDTO(location1, "TestLocationName1"),
                            new LocationDTO(location2, "TestLocationName2")
                    ));
        }

        @Test
        void shouldReturnEmptyLocationList() {
            when(locationDAO.findAll()).thenReturn(List.of());
            assertThat(locationService.getAllLocations())
                    .isNotNull()
                    .isEmpty();
        }
    }

    @Nested
    class WhenUpdateLocationById {
        private LocationRequest request;

        @BeforeEach
        void setUp() {
            request = new LocationRequest();
            request.setName("Sala nr2");
        }

        @Test
        void shouldThrowLocationNotFoundException() {
            when(locationDAO.findByLocationId(any())).thenReturn(null);

            assertThatThrownBy(() ->
                    locationService.updateLocationById(any(), request)
            ).isInstanceOf(LocationNotFoundException.class);
        }

        @Test
        void shouldThrowDuplicatedLocationNameException() {
            when(locationDAO.findByLocationId(any())).thenReturn(new LocationDocument());
            when(locationDAO.findByName(any())).thenReturn(new LocationDocument());

            assertThatThrownBy(() ->
                    locationService.updateLocationById(any(), request)
            ).isInstanceOf(DuplicatedLocationNameException.class);
        }

        @Test
        void shouldUpdateLocationName() throws DuplicatedLocationNameException, LocationNotFoundException {
            when(locationDAO.findByLocationId(any())).thenReturn(new LocationDocument());
            when(locationDAO.findByName(any())).thenReturn(null);

            String locationId = UUID.randomUUID().toString();
            when(locationDAO.save(any())).thenReturn(new LocationDocument(locationId, "Sala nr 2"));

            LocationDTO updatedLocation = locationService.updateLocationById(locationId, request);

            assertThat(updatedLocation.getLocationId()).isEqualTo(locationId);
            assertThat(updatedLocation.getName()).isEqualTo("Sala nr 2");
        }
    }

    @Nested
    class WhenRemoveLocation {

        @Test
        void shouldThrowLocationNotFoundException() {
            when(locationDAO.findByLocationId(any())).thenReturn(null);

            assertThatThrownBy(() ->
                    locationService.removeLocationById(any())
            ).isInstanceOf(LocationNotFoundException.class);
        }

        @Test
        void shouldRemoveLocation() throws LocationNotFoundException {
            String locationId = UUID.randomUUID().toString();
            when(locationDAO.findByLocationId(any()))
                    .thenReturn(new LocationDocument(locationId, "Sala nr 2"));

            LocationDTO removedLocation = locationService.removeLocationById(locationId);

            assertThat(removedLocation.getName()).isEqualTo("Sala nr 2");
            assertThat(removedLocation.getLocationId()).isEqualTo(locationId);
        }
    }

}