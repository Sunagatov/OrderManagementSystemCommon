package com.festiva.metrics;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
@ConditionalOnProperty(prefix = "kafka", name = "enabled", havingValue = "false", matchIfMissing = true)
public class NoOpMetricsSender implements MetricsSender {

    @Override
    public void sendMetric(String jsonMessage) {
        log.info("Kafka is disabled; ignoring metrics");
    }

    @Override
    public void produceMetric(Update update, String status, long processingTimeMillis) {
        log.info("Kafka is disabled; ignoring metrics");
    }
}
