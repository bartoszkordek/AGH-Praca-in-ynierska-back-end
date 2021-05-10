package com.healthy.gym.user.data.repository;

import com.healthy.gym.user.data.entity.RegistrationToken;
import com.healthy.gym.user.data.entity.ResetPasswordToken;
import com.healthy.gym.user.data.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ResetPasswordTokenDAOTest {

    private UserEntity janKowalskiEntity, andrzejNowakEntity;
    private ResetPasswordToken resetPasswordToken;
    private String testToken;
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private ResetPasswordTokenDAO resetPasswordTokenDAO;

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

        resetPasswordToken = new ResetPasswordToken(testToken, janKowalskiEntity);
        testEntityManager.persist(resetPasswordToken);
        testEntityManager.flush();
    }

    @AfterEach
    void tearDown() {
        userDAO.deleteAll();
        resetPasswordTokenDAO.deleteAll();
    }

    @Nested
    @DataJpaTest
    class WhenFindByTokenIsCalled {
        @Test
        void shouldReturnNullWhenProvidedTokenDoesNotExists() {
            ResetPasswordToken foundToken = resetPasswordTokenDAO.findByToken("sampleToken");
            assertThat(foundToken).isNull();
        }

        @Nested
        @DataJpaTest
        class WhenProvidedTokenExists {
            private ResetPasswordToken foundToken;

            @BeforeEach
            void setUp() {
                foundToken = resetPasswordTokenDAO.findByToken(testToken);
            }

            @Test
            void shouldNotReturnNull() {
                assertThat(foundToken).isNotNull();
            }

            @Test
            void shouldReturnResetPasswordTokenEntityWithProperToken() {
                assertThat(foundToken.getToken()).isEqualTo(testToken);
            }

            @Test
            void shouldReturnResetPasswordTokenEntityWithProperUserEntity() {
                UserEntity user = foundToken.getUserEntity();
                assertThat(user.getEmail()).isEqualTo(janKowalskiEntity.getEmail());
                assertThat(user.getUserId()).isEqualTo(janKowalskiEntity.getUserId());
            }

            @Test
            void shouldReturnResetPasswordTokenEntityWithProperExpiryDate() {
                assertThat(foundToken.getExpiryDate()).isEqualTo(resetPasswordToken.getExpiryDate());
            }
        }
    }

    @Nested
    @DataJpaTest
    class WhenFindByUserEntityIsCalled {
        @Test
        void shouldReturnNullWhenProvidedUserIsNotAssociatedWithAnyToken() {
            ResetPasswordToken foundToken = resetPasswordTokenDAO.findByUserEntity(andrzejNowakEntity);
            assertThat(foundToken).isNull();
        }

        @Nested
        @DataJpaTest
        class WhenProvidedUserEntityIsAssociatedWithAResetPasswordToken {
            private ResetPasswordToken foundToken;

            @BeforeEach
            void setUp() {
                foundToken = resetPasswordTokenDAO.findByUserEntity(janKowalskiEntity);
            }

            @Test
            void shouldNotReturnNull() {
                assertThat(foundToken).isNotNull();
            }

            @Test
            void shouldReturnResetPasswordEntityWithProperToken() {
                assertThat(foundToken.getToken()).isEqualTo(testToken);
            }

            @Test
            void shouldReturnResetPasswordTokenEntityWithProperUserEntity() {
                UserEntity user = foundToken.getUserEntity();
                assertThat(user.getEmail()).isEqualTo(janKowalskiEntity.getEmail());
                assertThat(user.getUserId()).isEqualTo(janKowalskiEntity.getUserId());
            }

            @Test
            void shouldReturnRegistrationTokenEntityWithProperExpiryDate() {
                assertThat(foundToken.getExpiryDate()).isEqualTo(resetPasswordToken.getExpiryDate());
            }
        }

    }

    @Nested
    @DataJpaTest
    class WhenSaveIsCalled {
        @Test
        void shouldSaveResetPasswordTokenInDB() {
            ResetPasswordToken token = new ResetPasswordToken(testToken, andrzejNowakEntity);
            resetPasswordTokenDAO.save(token);

            List<ResetPasswordToken> allTokens = resetPasswordTokenDAO.findAll();

            assertThat(allTokens.contains(token)).isTrue();
        }
    }
}