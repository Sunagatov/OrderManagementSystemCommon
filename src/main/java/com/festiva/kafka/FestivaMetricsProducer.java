package com.festiva.kafka;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@Slf4j
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FestivaMetricsProducer implements MetricsProducer {

    private final KafkaProducer<String, String> producer;
    private final String topic = "festiva.bot.metrics";

    public FestivaMetricsProducer(
            @Value("${kafka.bootstrap-servers}") String bootstrapServers,
            @Value("${kafka.api-key}") String apiKey,
            @Value("${kafka.api-secret}") String apiSecret) {

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
        log.info("Kafka Metrics Producer initialized.");
    }

    @Override
    public void sendMetric(String jsonMessage) {
        producer.send(new ProducerRecord<>(topic, jsonMessage));
    }

    @PreDestroy
    public void close() {
        producer.close();
    }
}
