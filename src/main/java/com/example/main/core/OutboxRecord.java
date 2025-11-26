package com.example.main.core;

<<<<<<< HEAD

import java.util.Map;

public class OutboxRecord {
    private Long id;
=======
public class OutboxRecord {
    private Object id;  // Changed to Object to support both Long (JDBC) and ObjectId (MongoDB)
>>>>>>> 3c4d80f (DB changes)
    private String payload;
    // you can add extra columns as needed

    public OutboxRecord() {}

<<<<<<< HEAD
    public OutboxRecord(Long id, String payload) {
=======
    public OutboxRecord(Object id, String payload) {
>>>>>>> 3c4d80f (DB changes)
        this.id = id;
        this.payload = payload;
    }

<<<<<<< HEAD
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
=======
    public Object getId() { return id; }
    public void setId(Object id) { this.id = id; }
>>>>>>> 3c4d80f (DB changes)
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
}
