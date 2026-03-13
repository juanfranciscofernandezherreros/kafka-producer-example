package com.fernandez.kafka.controller;

import com.fernandez.kafka.service.KafkaMessagePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/producer-app")
public class EventController {

    private static final int DEFAULT_MESSAGE_COUNT = 1_000_000;

    @Autowired
    private KafkaMessagePublisher publisher;

    @GetMapping("/publish/{message}")
    public ResponseEntity<?> publishMessage(@PathVariable String message) {
        try {
            // Envía 1.000.000 de mensajes usando batch linger para maximizar el rendimiento
            for (int i = 0; i < DEFAULT_MESSAGE_COUNT; i++) {
                publisher.sendMessageToTopic(message + " : " + i);
            }
            // Fuerza el envío de todos los mensajes pendientes en el buffer del productor
            publisher.flush();
            return ResponseEntity.ok("Messages published successfully.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Asynchronous bulk-send endpoint – best option for 1 M+ messages.
     * Returns HTTP 202 Accepted immediately while messages are dispatched
     * in a background thread, avoiding HTTP timeout on large volumes.
     *
     * @param baseMessage base text prepended to each message (default: "message")
     * @param count       number of messages to send (default: 1 000 000)
     */
    @PostMapping("/publish/bulk")
    public ResponseEntity<?> publishBulkMessages(
            @RequestParam(defaultValue = "message") String baseMessage,
            @RequestParam(defaultValue = "1000000") int count) {
        if (count < 1 || count > 10_000_000) {
            return ResponseEntity.badRequest()
                    .body("count must be between 1 and 10,000,000");
        }
        publisher.sendBulkMessages(baseMessage, count);
        return ResponseEntity.accepted()
                .body("Bulk message processing started. Sending " + count + " messages asynchronously.");
    }
}
