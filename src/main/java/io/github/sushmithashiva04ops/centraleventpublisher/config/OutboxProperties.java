package io.github.sushmithashiva04ops.centraleventpublisher.config;



import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "outbox")
public class OutboxProperties {

	private List<OutboxConfigItem> items = new ArrayList<>();

    public List<OutboxConfigItem> getItems() {
        return items;
    }

    public void setItems(List<OutboxConfigItem> items) {
        this.items = items;
    }

    public static class OutboxConfigItem {
        public String getTableName() {
			return tableName;
		}
		public void setTableName(String tableName) {
			this.tableName = tableName;
		}
		public long getPollingIntervalMs() {
			return pollingIntervalMs;
		}
		public void setPollingIntervalMs(long pollingIntervalMs) {
			this.pollingIntervalMs = pollingIntervalMs;
		}
		public String getPendingStatus() {
			return pendingStatus;
		}
		public void setPendingStatus(String pendingStatus) {
			this.pendingStatus = pendingStatus;
		}
		public String getSentStatus() {
			return sentStatus;
		}
		public void setSentStatus(String sentStatus) {
			this.sentStatus = sentStatus;
		}
		public String getQueueNamePublish() {
			return queueNamePublish;
		}
		public void setQueueNamePublish(String queueNamePublish) {
			this.queueNamePublish = queueNamePublish;
		}
		public String getQueueNameListen() {
			return queueNameListen;
		}
		public void setQueueNameListen(String queueNameListen) {
			this.queueNameListen = queueNameListen;
		}
		private String tableName;
        private long pollingIntervalMs = 3000;
        private String pendingStatus = "PENDING";
        private String sentStatus = "SENT";

        private String queueNamePublish;
        private String queueNameListen;
   	 private String brokerUrl;
   	private String fallbackBrokerUrl;

	    public String getFallbackBrokerUrl() {
		return fallbackBrokerUrl;
	}
	public void setFallbackBrokerUrl(String fallbackBrokerUrl) {
		this.fallbackBrokerUrl = fallbackBrokerUrl;
	}
		private String username;
	    public String getBrokerUrl() {
			return brokerUrl;
		}
		public void setBrokerUrl(String brokerUrl) {
			this.brokerUrl = brokerUrl;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		private String password;

        // getters + setters...
    }
}

