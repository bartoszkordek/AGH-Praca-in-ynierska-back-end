package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.data.document.NotificationDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.test.utils.TestDocumentUtilComponent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.healthy.gym.trainings.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
class NotificationServiceIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TestDocumentUtilComponent utilComponent;

    private List<UserDocument> users;
    private UserDocument mainUser;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        String userId = UUID.randomUUID().toString();
        mainUser = utilComponent.saveAndGetTestUser(userId);
        users = utilComponent.getTestListOfSavedUserDocuments(10);

        UsernamePasswordAuthenticationToken userAuth =
                new UsernamePasswordAuthenticationToken(userId, null);
        SecurityContextHolder.getContext().setAuthentication(userAuth);
        LocaleContextHolder.setLocale(Locale.ENGLISH);
    }

    @AfterEach
    void tearDown() {

        mongoTemplate.dropCollection(UserDocument.class);
        mongoTemplate.dropCollection(NotificationDocument.class);
    }

    @Test
    void shouldSendNotifications() {
        String title = "TestTitle";
        notificationService.sendNotificationsAndEmailsWhenUpdatingGroupTraining(
                title,
                LocalDateTime.of(2020, 10, 9, 12, 20, 30),
                users
        );

        var notifications = mongoTemplate.findAll(NotificationDocument.class);
        assertThat(notifications.size()).isEqualTo(10);

        var returnedUsers = notifications
                .stream()
                .map(NotificationDocument::getTo)
                .collect(Collectors.toList());

        assertThat(returnedUsers).isEqualTo(users);

        var setOfCreatedBy = notifications
                .stream()
                .map(NotificationDocument::getCreatedBy)
                .collect(Collectors.toSet());

        assertThat(setOfCreatedBy.size()).isEqualTo(1);
        assertThat(setOfCreatedBy.contains(mainUser)).isTrue();

        var titles = notifications
                .stream()
                .map(NotificationDocument::getTitle)
                .collect(Collectors.toSet());
        assertThat(titles.size()).isEqualTo(1);
        assertThat(titles.contains("TestTitle 2020-10-09 12:20")).isTrue();

        var content = notifications
                .stream()
                .map(NotificationDocument::getContent)
                .collect(Collectors.toSet());
        assertThat(content.size()).isEqualTo(1);
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedContent = messages.get("notification.group.training.update");
        assertThat(content.contains(expectedContent)).isTrue();
    }
}