package io.github.sushmithashiva04ops.centraleventpublisher.core;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class GenericOutboxRepositoryFactory {

    private final JdbcTemplate jdbc;

    public GenericOutboxRepositoryFactory(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public GenericOutboxRepository create(String tableName) {
        return new GenericOutboxRepository(jdbc, tableName);
    }
}
