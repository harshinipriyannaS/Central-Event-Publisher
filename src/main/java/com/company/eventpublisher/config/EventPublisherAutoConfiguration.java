package com.company.eventpublisher.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.company.eventpublisher.core.EventPoller;
import com.company.eventpublisher.core.EventPublisherService;
import com.company.eventpublisher.repository.OutboxRepository;
import com.company.eventpublisher.sender.JmsSender;
import com.company.eventpublisher.sender.JmsSenderImpl;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(EventPublisherProperties.class)
@Import({JmsConfig.class, ResilienceConfig.class, JdbcRepositoryConfig.class, MongoRepositoryConfig.class})
@ConditionalOnProperty(name = "event.publisher.enabled", havingValue = "true", matchIfMissing = true)
public class EventPublisherAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(EventPublisherAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("Central Event Publisher JAR initialized");
    }

    @Bean
    public JmsSender jmsSender(JmsTemplate jmsTemplate) {
        return new JmsSenderImpl(jmsTemplate);
    }

    @Bean
    public EventPublisherService eventPublisherService(
            JmsSender jmsSender,
            OutboxRepository repository,
            EventPublisherProperties properties,
            Retry retry,
            CircuitBreaker circuitBreaker) {
        return new EventPublisherService(jmsSender, repository, properties, retry, circuitBreaker);
    }

    @Bean
    public EventPoller eventPoller(
            OutboxRepository repository,
            EventPublisherService publisherService,
            EventPublisherProperties properties) {
        return new EventPoller(repository, publisherService, properties);
    }
}
