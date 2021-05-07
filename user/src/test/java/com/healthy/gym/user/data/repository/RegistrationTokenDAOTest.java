package com.healthy.gym.user.data.repository;

import com.healthy.gym.user.data.entity.RegistrationToken;
import com.healthy.gym.user.data.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RegistrationTokenDAOTest {

    private UserEntity janKowalskiEntity, andrzejNowakEntity;
    private RegistrationToken registrationToken;
    private String testToken;
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private RegistrationTokenDAO registrationTokenDAO;

    @BeforeEach
    void setUp() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        janKowalskiEntity = new UserEntity(
                "Jan",
                "Kowalski",
                "jan.kowalski@test.com",
                "666 777 888",
                bCryptPasswordEncoder.encode("password1234"),
                UUID.randomUUID().toString(),
                false
        );

        andrzejNowakEntity = new UserEntity(
                "Andrzej",
                "Nowak",
                "andrzej.nowak@test.com",
                "676 777 888",
                bCryptPasswordEncoder.encode("password4576"),
                UUID.randomUUID().toString(),
                true
        );

        testEntityManager.persist(janKowalskiEntity);
        testEntityManager.persist(andrzejNowakEntity);
        testEntityManager.flush();

        testToken = UUID.randomUUID().toString();

        registrationToken = new RegistrationToken(testToken, janKowalskiEntity);
        testEntityManager.persist(registrationToken);
        testEntityManager.flush();
    }

    @AfterEach
    void tearDown() {
        userDAO.deleteAll();
        registrationTokenDAO.deleteAll();
    }

    @Nested
    @DataJpaTest
    class WhenFindByTokenIsCalled {

        @Test
        void shouldReturnNullWhenProvidedTokenDoesNotExists() {
            RegistrationToken foundToken = registrationTokenDAO.findByToken("sampleToken");
            assertThat(foundToken).isNull();
        }

        @Nested
        @DataJpaTest
        class WhenProvidedTokenExists {
            private RegistrationToken foundToken;

            @BeforeEach
            void setUp() {
                foundToken = registrationTokenDAO.findByToken(testToken);
            }

            @Test
            void shouldNotReturnNull() {
                assertThat(foundToken).isNotNull();
            }

            @Test
            void shouldReturnRegistrationTokenEntityWithProperToken() {
                assertThat(foundToken.getToken()).isEqualTo(testToken);
            }

            @Test
            void shouldReturnRegistrationTokenEntityWithProperUserEntity() {
                UserEntity user = foundToken.getUserEntity();
                assertThat(user.getEmail()).isEqualTo(janKowalskiEntity.getEmail());
                assertThat(user.getUserId()).isEqualTo(janKowalskiEntity.getUserId());
            }

            @Test
            void shouldReturnRegistrationTokenEntityWithProperExpiryDate() {
                assertThat(foundToken.getExpiryDate()).isEqualTo(registrationToken.getExpiryDate());
            }
        }
    }

    @Nested
    @DataJpaTest
    class WhenFindByUserEntityIsCalled {
        @Test
        void shouldReturnNullWhenProvidedUserIsNotAssociatedWithAnyToken() {
            RegistrationToken foundToken = registrationTokenDAO.findByUserEntity(andrzejNowakEntity);
            assertThat(foundToken).isNull();
        }

        @Nested
        @DataJpaTest
        class WhenProvidedUserEntityIsAssociatedWithARegistrationTokenEntity {
            private RegistrationToken foundToken;

            @BeforeEach
            void setUp() {
                foundToken = registrationTokenDAO.findByUserEntity(janKowalskiEntity);
            }

            @Test
            void shouldNotReturnNull() {
                assertThat(foundToken).isNotNull();
            }

            @Test
            void shouldReturnRegistrationTokenEntityWithProperToken() {
                assertThat(foundToken.getToken()).isEqualTo(testToken);
            }

            @Test
            void shouldReturnRegistrationTokenEntityWithProperUserEntity() {
                UserEntity user = foundToken.getUserEntity();
                assertThat(user.getEmail()).isEqualTo(janKowalskiEntity.getEmail());
                assertThat(user.getUserId()).isEqualTo(janKowalskiEntity.getUserId());
            }

            @Test
            void shouldReturnRegistrationTokenEntityWithProperExpiryDate() {
                assertThat(foundToken.getExpiryDate()).isEqualTo(registrationToken.getExpiryDate());
            }
        }
    }
}