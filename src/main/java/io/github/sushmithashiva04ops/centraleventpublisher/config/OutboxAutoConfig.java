package io.github.sushmithashiva04ops.centraleventpublisher.config;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import io.github.sushmithashiva04ops.centraleventpublisher.core.GenericOutboxRepository;
import io.github.sushmithashiva04ops.centraleventpublisher.core.OutboxMessagePublisher;
import io.github.sushmithashiva04ops.centraleventpublisher.core.OutboxProcessor;

@Configuration
@EnableConfigurationProperties(OutboxProperties.class)
@ComponentScan(basePackages = "io.github.sushmithashiva04ops.centraleventpublisher")
public class OutboxAutoConfig {

    // --------------------------
    // ThreadPoolTaskScheduler Bean
    // --------------------------
    @Bean
    @ConditionalOnMissingBean(name = "outboxScheduler")
    public ThreadPoolTaskScheduler outboxScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(4);
        scheduler.setThreadNamePrefix("outbox-");
        return scheduler;
    }

    @Bean
    @ConditionalOnMissingBean
    public ActiveMQConnectionFactory activeMQConnectionFactory(OutboxProperties props) {
        if (props.getItems().isEmpty()) {
            throw new IllegalStateException("No outbox.items[] found in configuration");
        }

        OutboxProperties.OutboxConfigItem cfg = props.getItems().get(0);
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        String url = String.format(
            "failover:(%s,%s)?randomize=false&initialReconnectDelay=100&maxReconnectAttempts=0",
            cfg.getBrokerUrl(),
            cfg.getFallbackBrokerUrl()
        );
        factory.setBrokerURL(url);
        factory.setUserName(cfg.getUsername());
        factory.setPassword(cfg.getPassword());
        return factory;
    }

    @Bean
    @ConditionalOnMissingBean
    public JmsTemplate jmsTemplate(ActiveMQConnectionFactory factory) {
        return new JmsTemplate(factory);
    }

    @Bean
    public List<GenericOutboxRepository> repositories(DataSource ds, OutboxProperties props) {
        List<GenericOutboxRepository> repos = new ArrayList<>();
        for (OutboxProperties.OutboxConfigItem item : props.getItems()) {
            if (item.getTableName() == null || item.getTableName().isBlank()) {
                System.out.println("Skipping repository creation → tableName is NULL/EMPTY");
                continue;
            }
            repos.add(new GenericOutboxRepository(new JdbcTemplate(ds), item.getTableName()));
        }
        return repos;
    }

    @Bean
    public List<OutboxProcessor> processors(
            List<GenericOutboxRepository> repos,
            OutboxMessagePublisher publisher,
            OutboxProperties props,
            ThreadPoolTaskScheduler outboxScheduler
    ) {
        List<OutboxProcessor> processors = new ArrayList<>();

        int repoIndex = 0;
        for (OutboxProperties.OutboxConfigItem item : props.getItems()) {
            if (item.getTableName() == null || item.getTableName().isBlank()) {
                System.out.println("Skipping OutboxProcessor creation → tableName is NULL/EMPTY");
                continue;
            }

            GenericOutboxRepository repo = repos.get(repoIndex++);
            OutboxProcessor processor = new OutboxProcessor(repo, publisher, item, outboxScheduler);
            processor.start();
            processors.add(processor);
        }

        return processors;
    }
}
