package com.healthy.gym.user.shared;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

class UserDTOTest {

    private UserDTO userDTO1;
    private UserDTO userDTO2;

    @BeforeEach
    void setUp() {
        userDTO1 = new UserDTO();
        userDTO2 = new UserDTO();
    }

    @AfterEach
    void tearDown() {
        userDTO1 = null;
        userDTO2 = null;
    }

    @Test
    void twoEmptyUserDTOshouldBeEqual() {
        assertThat(userDTO1).isEqualTo(userDTO2);
    }

    @Test
    void userDTOhasAllNullField() {
        assertThat(userDTO1).hasAllNullFieldsOrProperties();
        assertThat(userDTO2).hasAllNullFieldsOrProperties();
    }

    @Test
    void twoEmptyUserDTOshouldHaveSameHashCode() {
        assertThat(userDTO1).hasSameHashCodeAs(userDTO2);
    }

    @Test
    void twoUserDTOWithSameFieldValuesShouldBeEqual() {
        userDTO1.setUserId("randomID");
        userDTO1.setName("John");
        userDTO2.setUserId("randomID");
        userDTO2.setName("John");
        assertThat(userDTO1).isEqualTo(userDTO2);
    }

    @Test
    void twoUserDTOWithSameFieldValuesShouldHaveSameHashCode() {
        userDTO1.setUserId("randomID");
        userDTO1.setName("John");
        userDTO2.setUserId("randomID");
        userDTO2.setName("John");
        assertThat(userDTO1).hasSameHashCodeAs(userDTO2);
    }

    @Test
    void twoUserDTOWithDifferentFieldValuesShouldNotBeEqual() {
        userDTO1.setUserId("randomID");
        userDTO1.setName("John");
        userDTO2.setUserId("randomID");
        userDTO2.setName("Johny");
        assertThat(userDTO1.hashCode()).isNotEqualTo(userDTO2.hashCode());
    }

    @Test
    void userDTOcomparedWithNullShouldNotBeEqual(){
        assertThat(userDTO1).isNotEqualTo(null);
        assertThat(userDTO2).isNotEqualTo(null);
    }
}