package com.example.main.core;


<<<<<<< HEAD
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

=======
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

>>>>>>> 3c4d80f (DB changes)
public class GenericOutboxRepository {

    private final JdbcTemplate jdbc;
    private final String tableName;
    private final String pendingStatusCol = "status";
    private final String payloadCol = "payload";
    private final String idCol = "id";

    public GenericOutboxRepository(JdbcTemplate jdbc, String tableName) {
        this.jdbc = jdbc;
        this.tableName = tableName;
    }

    /**
     * Fetch one pending record and lock it (to avoid double processing).
     * Uses FOR UPDATE SKIP LOCKED — works in recent MySQL/Postgres + others.
     * Annotated transactional so lock applies.
     */
    @Transactional
    public OutboxRecord fetchNextPending(String pendingStatus) {
        String sql = "SELECT " + idCol + ", " + payloadCol +
                     " FROM " + tableName +
                     " WHERE " + pendingStatusCol + " = ? " +
                     " ORDER BY " + idCol +
                     " LIMIT 1 FOR UPDATE SKIP LOCKED";

        List<OutboxRecord> rows = jdbc.query(sql, new Object[]{pendingStatus}, (rs, i) -> {
<<<<<<< HEAD
            Long id = rs.getLong(idCol);
=======
            Object id = rs.getLong(idCol);
>>>>>>> 3c4d80f (DB changes)
            String payload = rs.getString(payloadCol);
            return new OutboxRecord(id, payload);
        });

        return rows.isEmpty() ? null : rows.get(0);
    }

<<<<<<< HEAD
    public void updateStatus(Long id, String status) {
=======
    public void updateStatus(Object id, String status) {
>>>>>>> 3c4d80f (DB changes)
        String sql = "UPDATE " + tableName + " SET " + pendingStatusCol + " = ? WHERE " + idCol + " = ?";
        jdbc.update(sql, status, id);
    }
}
