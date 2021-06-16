package com.healthy.gym.trainings.enums;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GymRoleTest {
    @Nested
    class RoleAdmin {
        private String role;

        @BeforeEach
        void setUp() {
            role = "ROLE_ADMIN";
        }

        @Test
        void shouldReturnProperRole() {
            assertThat(GymRole.ADMIN.getRole()).isEqualTo(role);
        }

        @Test
        void shouldReturnProperAuthority() {
            assertThat(GymRole.ADMIN.getAuthority()).isEqualTo(role);
        }

        @Test
        void shouldReturnProperToString() {
            assertThat(GymRole.ADMIN).hasToString(role);
        }
    }

    @Nested
    class RoleEmployee {
        private String role;

        @BeforeEach
        void setUp() {
            role = "ROLE_EMPLOYEE";
        }

        @Test
        void shouldReturnProperRole() {
            assertThat(GymRole.EMPLOYEE.getRole()).isEqualTo(role);
        }

        @Test
        void shouldReturnProperAuthority() {
            assertThat(GymRole.EMPLOYEE.getAuthority()).isEqualTo(role);
        }

        @Test
        void shouldReturnProperToString() {
            assertThat(GymRole.EMPLOYEE).hasToString(role);
        }
    }

    @Nested
    class RoleTrainer {
        private String role;

        @BeforeEach
        void setUp() {
            role = "ROLE_TRAINER";
        }

        @Test
        void shouldReturnProperRole() {
            assertThat(GymRole.TRAINER.getRole()).isEqualTo(role);
        }

        @Test
        void shouldReturnProperAuthority() {
            assertThat(GymRole.TRAINER.getAuthority()).isEqualTo(role);
        }

        @Test
        void shouldReturnProperToString() {
            assertThat(GymRole.TRAINER).hasToString(role);
        }
    }

    @Nested
    class RoleUser {
        private String role;

        @BeforeEach
        void setUp() {
            role = "ROLE_USER";
        }

        @Test
        void shouldReturnProperRole() {
            assertThat(GymRole.USER.getRole()).isEqualTo(role);
        }

        @Test
        void shouldReturnProperAuthority() {
            assertThat(GymRole.USER.getAuthority()).isEqualTo(role);
        }

        @Test
        void shouldReturnProperToString() {
            assertThat(GymRole.USER).hasToString(role);
        }
    }

    @Nested
    class RoleManager {
        private String role;

        @BeforeEach
        void setUp() {
            role = "ROLE_MANAGER";
        }

        @Test
        void shouldReturnProperRole() {
            assertThat(GymRole.MANAGER.getRole()).isEqualTo(role);
        }

        @Test
        void shouldReturnProperAuthority() {
            assertThat(GymRole.MANAGER.getAuthority()).isEqualTo(role);
        }

        @Test
        void shouldReturnProperToString() {
            assertThat(GymRole.MANAGER).hasToString(role);
        }
    }
}