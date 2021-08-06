package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.GroupTrainingsDAO;
import com.healthy.gym.trainings.dto.BasicUserInfoDTO;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import com.healthy.gym.trainings.dto.ParticipantsDTO;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.healthy.gym.trainings.test.utils.TestDocumentUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EmployeeGroupTrainingServiceTest {

    private GroupTrainingsDAO groupTrainingsDAO;

    private EmployeeGroupTrainingService employeeGroupTrainingService;
    private String groupTrainingId;
    private GroupTrainingDocument groupTrainingDocument;
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        groupTrainingId = UUID.randomUUID().toString();
        groupTrainingsDAO = mock(GroupTrainingsDAO.class);
        employeeGroupTrainingService = new EmployeeGroupTrainingServiceImpl(groupTrainingsDAO);
        groupTrainingDocument = getGroupTrainingDocument();
        modelMapper = new ModelMapper();
    }

    private GroupTrainingDocument getGroupTrainingDocument() {
        return new GroupTrainingDocument(
                UUID.randomUUID().toString(),
                getTestTrainingType(),
                List.of(getTestTrainer(), getTestTrainer()),
                LocalDateTime.parse("2020-10-10T10:00"),
                LocalDateTime.parse("2020-10-10T12:00"),
                getTestLocation(),
                10,
                List.of(getTestUser(), getTestUser(), getTestUser()),
                List.of(getTestUser())
        );
    }

    @Nested
    class WhenGetTrainingParticipants {
        @Test
        void shouldThrowNotExistingGroupTrainingException() {
            when(groupTrainingsDAO.findFirstByGroupTrainingId(anyString())).thenReturn(null);

            assertThatThrownBy(
                    () -> employeeGroupTrainingService.getTrainingParticipants(groupTrainingId)
            ).isInstanceOf(NotExistingGroupTrainingException.class);
        }

        @Test
        void shouldReturnParticipants() throws NotExistingGroupTrainingException {
            when(groupTrainingsDAO.findFirstByGroupTrainingId(anyString()))
                    .thenReturn(groupTrainingDocument);
            List<BasicUserInfoDTO> basicList = mapDocumentListToDTOList(groupTrainingDocument.getBasicList());
            List<BasicUserInfoDTO> reserveList = mapDocumentListToDTOList(groupTrainingDocument.getReserveList());

            ParticipantsDTO participants = employeeGroupTrainingService.getTrainingParticipants(groupTrainingId);

            assertThat(participants).isEqualTo(new ParticipantsDTO(basicList, reserveList));
        }

        private List<BasicUserInfoDTO> mapDocumentListToDTOList(List<UserDocument> listToMapped) {
            return listToMapped
                    .stream()
                    .map(userDocument -> modelMapper.map(userDocument, BasicUserInfoDTO.class))
                    .collect(Collectors.toList());
        }
    }

    @Nested
    class WhenGetGroupTrainingById {
        @Test
        void shouldThrowNotExistingGroupTrainingException() {
            when(groupTrainingsDAO.findFirstByGroupTrainingId(anyString())).thenReturn(null);

            assertThatThrownBy(
                    () -> employeeGroupTrainingService.getGroupTrainingById(groupTrainingId)
            ).isInstanceOf(NotExistingGroupTrainingException.class);
        }

        @Test
        void shouldReturnGroupTrainingDTO() throws NotExistingGroupTrainingException {
            when(groupTrainingsDAO.findFirstByGroupTrainingId(anyString()))
                    .thenReturn(groupTrainingDocument);

            GroupTrainingDTO groupTrainingDTO = employeeGroupTrainingService.getGroupTrainingById(groupTrainingId);

            assertThat(groupTrainingDTO.getParticipants().getBasicList().size())
                    .isEqualTo(groupTrainingDocument.getBasicList().size());
            assertThat(groupTrainingDTO.getParticipants().getReserveList().size())
                    .isEqualTo(groupTrainingDocument.getReserveList().size());
            assertThat(groupTrainingDTO.getTrainers().size())
                    .isEqualTo(groupTrainingDocument.getTrainers().size());
            assertThat(groupTrainingDTO.getLocation()).isEqualTo(groupTrainingDocument.getLocation().getName());
        }
    }
}