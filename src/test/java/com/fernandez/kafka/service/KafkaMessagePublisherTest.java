package com.fernandez.kafka.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaMessagePublisherTest {

    @Mock
    private KafkaTemplate<String, Object> template;

    @InjectMocks
    private KafkaMessagePublisher publisher;

    private SendResult<String, Object> sendResult;

    @BeforeEach
    void setUp() {
        RecordMetadata metadata = new RecordMetadata(
                new TopicPartition("javatechie-demo-3", 0), 0, 0, 0L, 0, 0);
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>("javatechie-demo-3", "test");
        sendResult = new SendResult<>(producerRecord, metadata);
    }

    @Test
    void sendMessageToTopic_shouldSendMessageSuccessfully() {
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(sendResult);
        when(template.send(eq("javatechie-demo-3"), eq("hello"))).thenReturn(future);

        publisher.sendMessageToTopic("hello");

        verify(template, times(1)).send("javatechie-demo-3", "hello");
    }

    @Test
    void sendMessageToTopic_shouldHandleFailure() {
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka unavailable"));
        when(template.send(eq("javatechie-demo-3"), eq("fail-msg"))).thenReturn(future);

        publisher.sendMessageToTopic("fail-msg");

        verify(template, times(1)).send("javatechie-demo-3", "fail-msg");
    }

    @Test
    void flush_shouldDelegateToKafkaTemplate() {
        doNothing().when(template).flush();

        publisher.flush();

        verify(template, times(1)).flush();
    }
}
