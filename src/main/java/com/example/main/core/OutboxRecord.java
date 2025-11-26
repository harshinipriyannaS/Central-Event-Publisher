package com.example.main.core;


import java.util.Map;

public class OutboxRecord {
    private Long id;
    private String payload;
    // you can add extra columns as needed

    public OutboxRecord() {}

    public OutboxRecord(Long id, String payload) {
        this.id = id;
        this.payload = payload;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
}
