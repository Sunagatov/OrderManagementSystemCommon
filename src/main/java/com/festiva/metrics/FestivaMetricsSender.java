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
            @Value("${kafka.topic}") String topic) {

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
    public void sendMetrics(String jsonMessage) {
        producer.send(new ProducerRecord<>(topic, jsonMessage));
    }

    @Override
    public void sendMetrics(Update update, String status, long processingTimeMillis) {
        String jsonMessage = buildMetricsKafkaMessage(update, status, processingTimeMillis);
        sendMetrics(jsonMessage);
    }

    private String buildMetricsKafkaMessage(Update update, String status, long processingTimeMillis) {
        // Инициализируем значения по умолчанию
        String command = "unknown";
        String userName = "unknown";
        Long userId = null;
        if (update.hasMessage() && update.getMessage().getFrom() != null) {
            // Получаем текст сообщения
            String text = update.getMessage().getText() != null ? update.getMessage().getText().trim() : "";
            if (!text.isEmpty() && text.startsWith("/")) {
                String[] tokens = text.split("\\s+");
                command = tokens[0];
            } else {
                command = text;
            }
            userId = update.getMessage().getFrom().getId();
            userName = update.getMessage().getFrom().getUserName();
            if (userName == null || userName.isEmpty()) {
                userName = update.getMessage().getFrom().getFirstName();
                if (update.getMessage().getFrom().getLastName() != null) {
                    userName += " " + update.getMessage().getFrom().getLastName();
                }
            }
        } else if (update.hasCallbackQuery() && update.getCallbackQuery().getFrom() != null) {
            // Если обновление является callback-запросом
            userId = update.getCallbackQuery().getFrom().getId();
            command = update.getCallbackQuery().getData();
            userName = update.getCallbackQuery().getFrom().getUserName();
            if (userName == null || userName.isEmpty()) {
                userName = update.getCallbackQuery().getFrom().getFirstName();
                if (update.getCallbackQuery().getFrom().getLastName() != null) {
                    userName += " " + update.getCallbackQuery().getFrom().getLastName();
                }
            }
        }
        if (userId == null) {
            userId = 0L;
        }
        return String.format(
                "{\"timestamp\":\"%s\", \"userId\":\"%d\", \"userName\":\"%s\", \"command\":\"%s\", \"status\":\"%s\", \"processingTimeMillis\":%d}",
                Instant.now().toString(),
                userId,
                userName,
                command,
                status,
                processingTimeMillis);
    }

    @PreDestroy
    public void close() {
        producer.close();
    }
}
