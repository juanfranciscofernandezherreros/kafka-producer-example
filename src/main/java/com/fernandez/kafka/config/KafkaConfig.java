package com.fernandez.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class KafkaConfig {

    @Bean
    public NewTopic createTopic() {
        return TopicBuilder.name("javatechie-demo-3")
                .partitions(10)
                .replicas(1)
                .build();
    }
}
