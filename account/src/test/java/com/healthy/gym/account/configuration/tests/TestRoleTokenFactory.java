package com.healthy.gym.account.configuration.tests;

import com.healthy.gym.account.component.TokenManager;
import com.healthy.gym.account.enums.GymRole;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class TestRoleTokenFactory {

    private final TokenManager tokenManager;

    @Autowired
    public TestRoleTokenFactory(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    public String getAdminToken(String adminId) {
        List<String> roles = List.of(GymRole.USER.getRole(), GymRole.ADMIN.getRole());
        return getToken(adminId, roles);
    }

    public String getAdminToken() {
        String adminId = UUID.randomUUID().toString();
        List<String> roles = List.of(GymRole.USER.getRole(), GymRole.ADMIN.getRole());
        return getToken(adminId, roles);
    }

    public String getToken(String userId, List<String> roles) {
        return tokenManager.getTokenPrefix() + " " + Jwts.builder()
                .setSubject(userId)
                .claim("roles", roles)
                .setExpiration(setTokenExpirationTime())
                .signWith(tokenManager.getSignatureAlgorithm(), tokenManager.getSigningKey())
                .compact();
    }

    private Date setTokenExpirationTime() {
        long currentTime = System.currentTimeMillis();
        long expirationTime = tokenManager.getExpirationTimeInMillis();
        return new Date(currentTime + expirationTime);
    }

    public String getEmployeeToken(String employeeId) {
        List<String> roles = List.of(GymRole.USER.getRole(), GymRole.EMPLOYEE.getRole());
        return getToken(employeeId, roles);
    }

    public String getEmployeeToken() {
        String employeeId = UUID.randomUUID().toString();
        List<String> roles = List.of(GymRole.USER.getRole(), GymRole.EMPLOYEE.getRole());
        return getToken(employeeId, roles);
    }

    public String getMangerToken(String managerId) {
        List<String> roles = List.of(GymRole.USER.getRole(), GymRole.MANAGER.getRole());
        return getToken(managerId, roles);
    }

    public String getMangerToken() {
        String managerId = UUID.randomUUID().toString();
        List<String> roles = List.of(GymRole.USER.getRole(), GymRole.MANAGER.getRole());
        return getToken(managerId, roles);
    }

    public String getTrainerToken(String trainerId) {
        List<String> roles = List.of(GymRole.USER.getRole(), GymRole.TRAINER.getRole());
        return getToken(trainerId, roles);
    }

    public String getTrainerToken() {
        String trainerId = UUID.randomUUID().toString();
        List<String> roles = List.of(GymRole.USER.getRole(), GymRole.TRAINER.getRole());
        return getToken(trainerId, roles);
    }

    public String getUserToken(String userId) {
        List<String> roles = List.of(GymRole.USER.getRole());
        return getToken(userId, roles);
    }

    public String getUserToken() {
        String userId = UUID.randomUUID().toString();
        List<String> roles = List.of(GymRole.USER.getRole());
        return getToken(userId, roles);
    }
}
