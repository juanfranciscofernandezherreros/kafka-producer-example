package com.javatechie.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KafkaConfigTest {

    @Test
    void createTopic_shouldReturnNewTopicWithCorrectName() {
        KafkaConfig config = new KafkaConfig();
        NewTopic topic = config.createTopic();

        assertEquals("javatechie-demo-3", topic.name());
        assertEquals(5, topic.numPartitions());
        assertEquals(1, topic.replicationFactor());
    }
}
