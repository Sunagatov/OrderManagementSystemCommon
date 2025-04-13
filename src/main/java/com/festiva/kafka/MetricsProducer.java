package com.festiva.kafka;

public interface MetricsProducer {

    void sendMetric(String jsonMessage);
}
