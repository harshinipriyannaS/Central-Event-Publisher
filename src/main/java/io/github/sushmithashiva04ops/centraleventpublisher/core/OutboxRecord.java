package io.github.sushmithashiva04ops.centraleventpublisher.core;


import java.util.UUID;

public class OutboxRecord {
    public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
	private UUID id;
    private String payload;
    

    public OutboxRecord() {}

    public OutboxRecord(UUID id, String payload) {
        this.id = id;
        this.payload = payload;
    }

  
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
}
