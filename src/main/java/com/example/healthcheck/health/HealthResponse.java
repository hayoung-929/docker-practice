package com.example.healthcheck.health;

public record HealthResponse(
        String status,
        String mariadb,
        String redis
) {
}