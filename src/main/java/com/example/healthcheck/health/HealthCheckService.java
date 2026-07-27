package com.example.healthcheck.health;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class HealthCheckService {

    private final JdbcTemplate jdbcTemplate;
    private final RedisConnectionFactory redisConnectionFactory;

    public HealthCheckService(
            JdbcTemplate jdbcTemplate,
            RedisConnectionFactory redisConnectionFactory
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.redisConnectionFactory = redisConnectionFactory;
    }

    public HealthResponse check() {
        boolean mariadbUp = checkMariaDb();
        boolean redisUp = checkRedis();

        String status = mariadbUp && redisUp ? "UP" : "DOWN";

        return new HealthResponse(
                status,
                mariadbUp ? "UP" : "DOWN",
                redisUp ? "UP" : "DOWN"
        );
    }

    private boolean checkMariaDb() {
        try {
            Integer result = jdbcTemplate.queryForObject(
                    "SELECT 1",
                    Integer.class
            );
            return Integer.valueOf(1).equals(result);
        } catch (RuntimeException exception) {
            return false;
        }
    }

    private boolean checkRedis() {
        try (RedisConnection connection =
                     redisConnectionFactory.getConnection()) {
            return "PONG".equalsIgnoreCase(connection.ping());
        } catch (RuntimeException exception) {
            return false;
        }
    }
}