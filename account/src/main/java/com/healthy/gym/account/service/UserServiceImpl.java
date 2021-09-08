package com.healthy.gym.account.service;

import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.data.repository.UserDAO;
import com.healthy.gym.account.dto.DetailUserInfoDTO;
import com.healthy.gym.account.dto.StatsDTO;
import com.healthy.gym.account.enums.GymRole;
import com.healthy.gym.account.exception.NoUserFound;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final String SURNAME = "surname";
    private final UserDAO userDAO;
    private final ModelMapper modelMapper;
    private final Clock clock;

    public UserServiceImpl(UserDAO userDAO, Clock clock) {
        this.userDAO = userDAO;
        this.modelMapper = new ModelMapper();
        this.clock = clock;
    }

    @Override
    public List<DetailUserInfoDTO> getAllUsersInSystem() throws NoUserFound {
        var userList = userDAO.findAll(Sort.by(SURNAME));
        if (userList.isEmpty()) throw new NoUserFound();

        return userList
                .stream()
                .filter(userDocument -> !userDocument.getEmail().equals("root"))
                .map(userDocument -> modelMapper.map(userDocument, DetailUserInfoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<DetailUserInfoDTO> getAllTrainersInSystem() throws NoUserFound {
        var userList = userDAO
                .findAllByGymRolesContains(GymRole.TRAINER, Sort.by(SURNAME));
        if (userList.isEmpty()) throw new NoUserFound();

        return userList
                .stream()
                .map(userDocument -> modelMapper.map(userDocument, DetailUserInfoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<DetailUserInfoDTO> getAllEmployeesInSystem() throws NoUserFound {
        var userList = userDAO
                .findAllByGymRolesContains(GymRole.EMPLOYEE, Sort.by(SURNAME));
        if (userList.isEmpty()) throw new NoUserFound();

        return userList
                .stream()
                .map(userDocument -> modelMapper.map(userDocument, DetailUserInfoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<DetailUserInfoDTO> getAllManagersInSystem() throws NoUserFound {
        var userList = userDAO
                .findAllByGymRolesContains(GymRole.MANAGER, Sort.by(SURNAME));
        if (userList.isEmpty()) throw new NoUserFound();

        return userList
                .stream()
                .map(userDocument -> modelMapper.map(userDocument, DetailUserInfoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StatsDTO> getLastWeekStats() {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDate today = now.toLocalDate();
        LocalDate sevenDaysAgo = today.minusDays(7);
        LocalDateTime todayDateTime = LocalDateTime.of(today, LocalTime.MAX);
        LocalDateTime sevenDaysAgoDateTime = LocalDateTime.of(sevenDaysAgo, LocalTime.MIN);

        var userList = userDAO
                .findAllByCreatedAtBetween(
                        sevenDaysAgoDateTime,
                        todayDateTime,
                        Sort.by("createdAt")
                );

        return userList
                .stream()
                .map(userDocument -> userDocument.getCreatedAt().toLocalDate())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .map(
                        localDateTimeLongEntry -> new StatsDTO(
                                localDateTimeLongEntry.getKey(),
                                localDateTimeLongEntry.getValue().intValue())
                )
                .sorted(Comparator.comparing(StatsDTO::getCreatedAt))
                .collect(Collectors.toList());
    }
}
