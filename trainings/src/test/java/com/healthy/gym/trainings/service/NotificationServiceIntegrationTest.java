package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.data.document.NotificationDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.test.utils.TestDocumentUtilComponent;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
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

import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.healthy.gym.trainings.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false",
        "spring.mail.host=localhost",
        "spring.mail.port=3025",
        "spring.mail.personal=AGH Praca inzynierska - Tests",
        "spring.mail.username=testEmailUsername",
        "spring.mail.password=password4Tests"
})
class NotificationServiceIntegrationTest {

    private static final ServerSetup serverSetup = new ServerSetup(3025, "localhost", "smtp");

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(serverSetup)
            .withConfiguration(
                    GreenMailConfiguration
                            .aConfig()
                            .withUser("testEmailUsername", "password4Tests")
            );

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
    private List<NotificationDocument> notifications;

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

    @Nested
    class ShouldSendNotificationsWithoutEmailsWhenUpdateGroupTraining {

        @BeforeEach
        void setUp() {
            notificationService.sendNotificationsAndEmailsWhenUpdatingGroupTraining(
                    "TestTitle",
                    LocalDateTime.of(2020, 10, 9, 12, 20, 30),
                    users,
                    false
            );

            notifications = mongoTemplate.findAll(NotificationDocument.class);
        }

        @Test
        void shouldSendEmail() {
            await().atMost(10, TimeUnit.SECONDS).untilAsserted(
                    () -> {
                        MimeMessage[] messages = greenMail.getReceivedMessages();
                        assertThat(messages).isEmpty();
                    }
            );
        }

        @Test
        void shouldContainsAllUsers() {
            assertThat(notifications.size()).isEqualTo(10);
            var returnedUsers = notifications
                    .stream()
                    .map(NotificationDocument::getTo)
                    .collect(Collectors.toList());

            assertThat(returnedUsers).isEqualTo(users);
        }

        @Test
        void shouldHaveTheSameUserWhoSendNotifications() {
            var setOfCreatedBy = notifications
                    .stream()
                    .map(NotificationDocument::getCreatedBy)
                    .collect(Collectors.toSet());

            assertThat(setOfCreatedBy.size()).isEqualTo(1);
            assertThat(setOfCreatedBy.contains(mainUser)).isTrue();
        }

        @Test
        void shouldContainSameTitle() {
            var titles = notifications
                    .stream()
                    .map(NotificationDocument::getTitle)
                    .collect(Collectors.toSet());

            assertThat(titles.size()).isEqualTo(1);
            assertThat(titles.contains("TestTitle 2020-10-09 12:20")).isTrue();
        }

        @Test
        void shouldContainSameContent() {
            var content = notifications
                    .stream()
                    .map(NotificationDocument::getContent)
                    .collect(Collectors.toSet());

            Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
            String expectedContent = messages.get("notification.group.training.update");

            assertThat(content.size()).isEqualTo(1);
            assertThat(content.contains(expectedContent)).isTrue();
        }
    }

    @Nested
    class ShouldSendNotificationsWithEmailsWhenUpdateGroupTraining {

        @BeforeEach
        void setUp() {
            notificationService.sendNotificationsAndEmailsWhenUpdatingGroupTraining(
                    "TestTitle",
                    LocalDateTime.of(2020, 10, 9, 12, 20, 30),
                    users,
                    true
            );

            notifications = mongoTemplate.findAll(NotificationDocument.class);
        }

        @Test
        void shouldSendEmail() {
            Map<String, String> responseMessages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
            String expectedMessage = responseMessages.get("notification.group.training.update");

            await().atMost(10, TimeUnit.SECONDS).untilAsserted(
                    () -> {
                        MimeMessage[] messages = greenMail.getReceivedMessages();
                        assertThat(messages).hasSize(10);

                        Set<String> currentRecipients = new HashSet<>();
                        for (MimeMessage message : messages) {
                            assertThat(message.getSubject()).isEqualTo("TestTitle 2020-10-09 12:20");
                            assertThat(message.getContent().toString().trim()).isEqualTo(expectedMessage);

                            String recipient = message.getAllRecipients()[0].toString();
                            currentRecipients.add(recipient);
                        }

                        Set<String> expectedRecipients = users
                                .stream()
                                .map(UserDocument::getEmail)
                                .collect(Collectors.toSet());

                        assertThat(currentRecipients).isEqualTo(expectedRecipients);
                    }
            );
        }

        @Test
        void shouldContainsAllUsers() {
            assertThat(notifications.size()).isEqualTo(10);
            var returnedUsers = notifications
                    .stream()
                    .map(NotificationDocument::getTo)
                    .collect(Collectors.toList());

            assertThat(returnedUsers).isEqualTo(users);
        }
    }
}