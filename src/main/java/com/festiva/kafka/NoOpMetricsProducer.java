package com.festiva.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "false")
public class NoOpMetricsProducer implements MetricsProducer {

    @Override
    public void sendMetric(String jsonMessage) {
        log.info("Kafka is disabled; ignoring metric: {}", jsonMessage);
    }
}
