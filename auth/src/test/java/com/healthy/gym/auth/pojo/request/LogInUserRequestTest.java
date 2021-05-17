package com.healthy.gym.auth.pojo.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class LogInUserRequestTest {

    @Test
    void shouldProperlySerializeReceivedLogInUserRequest() throws JsonProcessingException {
        String logInRequestJson=
                "{\n" +
                "\"email\" : \"jan.kowalski@wp.pl\",\n" +
                "\"password\" : \"secretP@ssword123\"\n" +
                "}";

        ObjectMapper mapper=new ObjectMapper();
        LogInUserRequest logInRequest=mapper.readValue(logInRequestJson,LogInUserRequest.class);

        assertThat(logInRequest.getEmail()).isEqualTo("jan.kowalski@wp.pl");
        assertThat(logInRequest.getPassword()).isEqualTo("secretP@ssword123");
    }

    @Test
    void shouldProperlySerializeReceivedLogInUserRequest2() throws JsonProcessingException {
        String logInRequestJson=
                "{\n" +
                        "\"email\" : \"jan.kowalski@wp.pl\",\n" +
                        "\"password\" : \"secretP@ssword123\",\n" +
                        "\"extraField\" : \"someText\"\n" +
                        "}";

        ObjectMapper mapper=new ObjectMapper();
        LogInUserRequest logInRequest=mapper.readValue(logInRequestJson,LogInUserRequest.class);

        assertThat(logInRequest.getEmail()).isEqualTo("jan.kowalski@wp.pl");
        assertThat(logInRequest.getPassword()).isEqualTo("secretP@ssword123");
    }
}