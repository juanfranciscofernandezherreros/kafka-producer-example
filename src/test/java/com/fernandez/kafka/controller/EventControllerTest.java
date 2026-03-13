package com.fernandez.kafka.controller;

import com.fernandez.kafka.service.KafkaMessagePublisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        doNothing().when(publisher).flush();

        mockMvc.perform(get("/producer-app/publish/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Messages published successfully."));

        verify(publisher, times(1_000_000)).sendMessageToTopic(anyString());
        verify(publisher, times(1)).flush();
    }

    @Test
    void publishMessage_shouldReturnInternalServerErrorOnException() throws Exception {
        doThrow(new RuntimeException("Kafka error")).when(publisher).sendMessageToTopic(anyString());

        mockMvc.perform(get("/producer-app/publish/hello"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void publishBulkMessages_shouldReturnAccepted() throws Exception {
        doNothing().when(publisher).sendBulkMessages(anyString(), anyInt());

        mockMvc.perform(post("/producer-app/publish/bulk")
                        .param("baseMessage", "test")
                        .param("count", "100"))
                .andExpect(status().isAccepted())
                .andExpect(content().string(
                        "Bulk message processing started. Sending 100 messages asynchronously."));

        verify(publisher, times(1)).sendBulkMessages("test", 100);
    }

    @Test
    void publishBulkMessages_shouldUseDefaultsWhenNoParamsProvided() throws Exception {
        doNothing().when(publisher).sendBulkMessages(anyString(), anyInt());

        mockMvc.perform(post("/producer-app/publish/bulk"))
                .andExpect(status().isAccepted());

        verify(publisher, times(1)).sendBulkMessages("message", 1_000_000);
    }

    @Test
    void publishBulkMessages_shouldReturnBadRequestForInvalidCount() throws Exception {
        mockMvc.perform(post("/producer-app/publish/bulk")
                        .param("count", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("count must be between 1 and 10,000,000"));

        mockMvc.perform(post("/producer-app/publish/bulk")
                        .param("count", "10000001"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("count must be between 1 and 10,000,000"));
    }
}
