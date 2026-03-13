package com.fernandez.kafka.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.fernandez.kafka.controller.EventController;
import com.fernandez.kafka.service.KafkaMessagePublisher;

@CucumberContextConfiguration
@WebMvcTest(EventController.class)
public class CucumberSpringConfiguration {

    @MockBean
    private KafkaMessagePublisher publisher;
}
