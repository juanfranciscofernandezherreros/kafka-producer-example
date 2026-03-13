package com.javatechie.controller;

import com.javatechie.service.KafkaMessagePublisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KafkaMessagePublisher publisher;

    @Test
    void publishMessage_shouldReturnOk() throws Exception {
        doNothing().when(publisher).sendMessageToTopic(anyString());

        mockMvc.perform(get("/producer-app/publish/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Messages published successfully."));

        verify(publisher, times(10001)).sendMessageToTopic(anyString());
    }

    @Test
    void publishMessage_shouldReturnInternalServerErrorOnException() throws Exception {
        doThrow(new RuntimeException("Kafka error")).when(publisher).sendMessageToTopic(anyString());

        mockMvc.perform(get("/producer-app/publish/hello"))
                .andExpect(status().isInternalServerError());
    }
}
