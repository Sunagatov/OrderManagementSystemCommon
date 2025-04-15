package com.festiva.metrics;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MetricsSender {
    void sendMetric(String jsonMessage);
    void produceMetric(Update update, String status, long processingTimeMillis);
}
