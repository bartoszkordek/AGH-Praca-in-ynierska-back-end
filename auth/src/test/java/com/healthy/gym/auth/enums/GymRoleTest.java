package com.healthy.gym.auth.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GymRoleTest {

    @Test
    void shouldReturnProperAdminRole() {
        assertThat(GymRole.ADMIN.getRole()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void shouldReturnProperEmployeeRole() {
        assertThat(GymRole.EMPLOYEE.getRole()).isEqualTo("ROLE_EMPLOYEE");
    }

    @Test
    void shouldReturnProperTrainerRole() {
        assertThat(GymRole.TRAINER.getRole()).isEqualTo("ROLE_TRAINER");
    }

    @Test
    void shouldReturnProperUserRole() {
        assertThat(GymRole.USER.getRole()).isEqualTo("ROLE_USER");
    }
}