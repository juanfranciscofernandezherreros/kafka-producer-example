package com.fernandez.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class KafkaMessagePublisher {

    private static final String TOPIC = "javatechie-demo-3";
    private static final int LOG_INTERVAL = 100_000;

    @Autowired
    private KafkaTemplate<String, Object> template;

    public void sendMessageToTopic(String message) {
        template.send(TOPIC, message);
    }

    /**
     * Sends {@code totalCount} messages asynchronously using key-based routing
     * so that records are spread evenly across all topic partitions.
     * The method runs in a dedicated thread pool (configured via @EnableAsync)
     * and returns immediately to the caller, allowing the HTTP response to be
     * sent back without waiting for all Kafka acknowledgements.
     */
    @Async
    public void sendBulkMessages(String baseMessage, int totalCount) {
        AtomicLong sent = new AtomicLong(0);
        for (int i = 0; i < totalCount; i++) {
            String key = String.valueOf(i);
            String msg = baseMessage + " : " + i;
            template.send(TOPIC, key, msg)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.warn("Failed to send message: {}", ex.getMessage());
                        } else {
                            long progress = sent.incrementAndGet();
                            if (progress % LOG_INTERVAL == 0) {
                                log.info("Progress: {} / {} messages sent", progress, totalCount);
                            }
                        }
                    });
        }
        template.flush();
        log.info("Bulk send complete: {} messages queued and flushed to topic {}", totalCount, TOPIC);
    }

    public void flush() {
        template.flush();
    }
}
