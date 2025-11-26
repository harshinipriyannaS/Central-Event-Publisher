package com.example.main.config;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;

import com.example.main.core.GenericOutboxRepository;
import com.example.main.core.OutboxMessagePublisher;
import com.example.main.core.OutboxProcessor;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(OutboxProperties.class)
@ComponentScan(basePackages = "com.example.outbox") // picks up ActiveMqOutboxPublisher
public class OutboxAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public ActiveMQConnectionFactory activeMQConnectionFactory(OutboxProperties props) {
        ActiveMQConnectionFactory f = new ActiveMQConnectionFactory();
        f.setBrokerURL(props.getBrokerUrl());
        if (props.getUsername() != null) f.setUserName(props.getUsername());
        if (props.getPassword() != null) f.setPassword(props.getPassword());
        return f;
    }

    @Bean
    @ConditionalOnMissingBean
    public JmsTemplate jmsTemplate(ActiveMQConnectionFactory factory) {
        return new JmsTemplate(factory);
    }

    @Bean
    @ConditionalOnMissingBean
    public GenericOutboxRepository genericOutboxRepository(DataSource ds, OutboxProperties props) {
        return new GenericOutboxRepository(new JdbcTemplate(ds), props.getTableName());
    }

    @Bean
    @ConditionalOnMissingBean
    public OutboxProcessor outboxProcessor(GenericOutboxRepository repo,
                                          OutboxMessagePublisher publisher,
                                          OutboxProperties props) {
        return new OutboxProcessor(repo, publisher, props);
    }
}
