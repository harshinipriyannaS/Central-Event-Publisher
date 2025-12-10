package io.github.sushmithashiva04ops.centraleventpublisher.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import io.github.sushmithashiva04ops.centraleventpublisher.core.GenericOutboxRepository;
import io.github.sushmithashiva04ops.centraleventpublisher.core.GenericOutboxRepositoryFactory;
import io.github.sushmithashiva04ops.centraleventpublisher.core.OutboxProcessor;
import io.github.sushmithashiva04ops.centraleventpublisher.publisher.ActiveMqOutboxPublisher;

@Configuration
public class OutboxProcessorConfig {

    private final ActiveMqOutboxPublisher publisher;
    private final GenericOutboxRepositoryFactory repositoryFactory;
    private final OutboxProperties outboxProperties;

    public OutboxProcessorConfig(
            ActiveMqOutboxPublisher publisher,
            GenericOutboxRepositoryFactory repositoryFactory,
            OutboxProperties outboxProperties
    ) {
        this.publisher = publisher;
        this.repositoryFactory = repositoryFactory;
        this.outboxProperties = outboxProperties;
    }

    @Bean
    public List<OutboxProcessor> outboxProcessors(ThreadPoolTaskScheduler outboxScheduler) {
        List<OutboxProcessor> processors = new ArrayList<>();

        for (OutboxProperties.OutboxConfigItem item : outboxProperties.getItems()) {

            // Skip null/empty tableName
            if (item.getTableName() == null || item.getTableName().isBlank()) {
                System.out.println("Skipping OutboxProcessor creation → tableName is NULL/EMPTY");
                continue;
            }

            GenericOutboxRepository repo = repositoryFactory.create(item.getTableName());
            OutboxProcessor processor = new OutboxProcessor(repo, publisher, item, outboxScheduler);
            processor.start();
            processors.add(processor);
        }

        return processors;
    }
}
