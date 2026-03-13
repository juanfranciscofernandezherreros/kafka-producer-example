package com.javatechie.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.javatechie.controller.EventController;
import com.javatechie.service.KafkaMessagePublisher;

@CucumberContextConfiguration
@WebMvcTest(EventController.class)
public class CucumberSpringConfiguration {

    @MockBean
    private KafkaMessagePublisher publisher;
}
