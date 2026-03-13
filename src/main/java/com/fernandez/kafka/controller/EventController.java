package com.fernandez.kafka.controller;

import com.fernandez.kafka.service.KafkaMessagePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/producer-app")
public class EventController {

    @Autowired
    private KafkaMessagePublisher publisher;

    @GetMapping("/publish/{message}")
    public ResponseEntity<?> publishMessage(@PathVariable String message) {
        try {
            // Envía 1.000.000 de mensajes usando batch linger para maximizar el rendimiento
            for (int i = 0; i < 1_000_000; i++) {
                publisher.sendMessageToTopic(message + " : " + i);
            }
            // Fuerza el envío de todos los mensajes pendientes en el buffer del productor
            publisher.flush();
            return ResponseEntity.ok("Messages published successfully.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
