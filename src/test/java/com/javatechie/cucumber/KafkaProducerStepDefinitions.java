package com.javatechie.cucumber;

import com.javatechie.service.KafkaMessagePublisher;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class KafkaProducerStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KafkaMessagePublisher publisher;

    private MvcResult mvcResult;
    private boolean messageSentSuccessfully;

    @Given("the Kafka producer service is available")
    public void theKafkaProducerServiceIsAvailable() {
        doNothing().when(publisher).sendMessageToTopic(anyString());
    }

    @Given("the Kafka producer service throws an error")
    public void theKafkaProducerServiceThrowsAnError() {
        doThrow(new RuntimeException("Kafka unavailable")).when(publisher).sendMessageToTopic(anyString());
    }

    @When("I publish a message {string} to the topic")
    public void iPublishAMessageToTheTopic(String message) {
        try {
            publisher.sendMessageToTopic(message);
            messageSentSuccessfully = true;
        } catch (Exception e) {
            messageSentSuccessfully = false;
        }
    }

    @When("I publish a message {string} via the REST endpoint")
    public void iPublishAMessageViaTheRestEndpoint(String message) throws Exception {
        mvcResult = mockMvc.perform(get("/producer-app/publish/" + message))
                .andReturn();
    }

    @Then("the message should be sent successfully")
    public void theMessageShouldBeSentSuccessfully() {
        assertTrue(messageSentSuccessfully);
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        assertNotNull(mvcResult);
        assertEquals(expectedStatus, mvcResult.getResponse().getStatus());
    }

    @Then("the response body should be {string}")
    public void theResponseBodyShouldBe(String expectedBody) throws Exception {
        assertNotNull(mvcResult);
        assertEquals(expectedBody, mvcResult.getResponse().getContentAsString());
    }
}
