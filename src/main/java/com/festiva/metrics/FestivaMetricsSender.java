package com.festiva.metrics;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.Instant;
import java.util.Properties;

@Component
@Slf4j
@ConditionalOnProperty(prefix = "kafka", name = "enabled", havingValue = "true", matchIfMissing = false)
public class FestivaMetricsSender implements MetricsSender {

    private final KafkaProducer<String, String> producer;
    private final String topic;

    public FestivaMetricsSender(
            @Value("${kafka.bootstrap-servers}") String bootstrapServers,
            @Value("${kafka.api-key}") String apiKey,
            @Value("${kafka.api-secret}") String apiSecret,
            @Value("${kafka.topic:festiva.bot.metrics}") String topic) {

        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("security.protocol", "SASL_SSL");
        props.put("sasl.mechanism", "PLAIN");
        props.put("sasl.jaas.config",
                "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                        "username=\"" + apiKey + "\" " +
                        "password=\"" + apiSecret + "\";");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        this.producer = new KafkaProducer<>(props);
        this.topic = topic;
        log.info("Kafka Metrics Producer initialized with topic {}", topic);
    }

    @Override
    public void sendMetric(String jsonMessage) {
        producer.send(new ProducerRecord<>(topic, jsonMessage));
    }

    @Override
    public void produceMetric(Update update, String status, long processingTimeMillis) {
        Long userId = null;
        String command = "";
        if (update.hasMessage() && update.getMessage().getFrom() != null) {
            userId = update.getMessage().getFrom().getId();
            if (update.getMessage().hasText()) {
                String text = update.getMessage().getText().trim();
                String[] parts = text.split("\\s+");
                if (parts.length > 0) {
                    command = parts[0];
                }
            }
        } else if (update.hasCallbackQuery() && update.getCallbackQuery().getFrom() != null) {
            userId = update.getCallbackQuery().getFrom().getId();
            command = update.getCallbackQuery().getData();
        }
        String jsonMessage = String.format(
                "{\"timestamp\":\"%s\", \"userId\":\"%s\", \"command\":\"%s\", \"status\":\"%s\", \"processingTimeMillis\":%d}",
                Instant.now().toString(),
                userId != null ? userId.toString() : "unknown",
                command,
                status,
                processingTimeMillis
        );
        sendMetric(jsonMessage);
    }

    @PreDestroy
    public void close() {
        producer.close();
    }
}
