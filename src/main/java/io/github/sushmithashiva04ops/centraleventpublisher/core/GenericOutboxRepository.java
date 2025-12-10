package io.github.sushmithashiva04ops.centraleventpublisher.core;

import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

public class GenericOutboxRepository {

    private final JdbcTemplate jdbc;
    private final String tableName;

    public GenericOutboxRepository(JdbcTemplate jdbc, String tableName) {
        this.jdbc = jdbc;
        this.tableName = tableName;
    }

    @Transactional
    public OutboxRecord fetchNextPending(String pendingStatus) {
        String sql =
                "SELECT id, payload " +
                "FROM " + tableName + " " +
                "WHERE status = ? " +
                "ORDER BY id " +
                "LIMIT 1 FOR UPDATE SKIP LOCKED";

        return jdbc.query(sql, new Object[]{pendingStatus}, rs -> {
            if (rs.next()) {
                UUID id = UUID.fromString(rs.getString("id"));
                System.out.println("Fetched row ID = " + id);
                
                String payload = rs.getString("payload");
                System.out.println("Fetched row ID = " + payload);
                return new OutboxRecord(id, payload);
            }
            return null;
        });
    }

    public void updateStatus(UUID id, String status) {
        String sql =
                "UPDATE " + tableName + " " +
                "SET status = ? " +
                "WHERE id = ?";

        jdbc.update(sql, status, id);
    }
}
