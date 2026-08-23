package com.flightops.platform.service;

import com.flightops.platform.event.FlightEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class FlightEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlightEventPublisher.class);

    private final KafkaTemplate<String, FlightEvent> kafkaTemplate;
    private final boolean kafkaEnabled;
    private final String topicName;

    public FlightEventPublisher(KafkaTemplate<String, FlightEvent> kafkaTemplate,
            @Value("${app.kafka.enabled:true}") boolean kafkaEnabled,
            @Value("${app.kafka.topic}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaEnabled = kafkaEnabled;
        this.topicName = topicName;
    }

    public void publish(FlightEvent event) {
        if (!kafkaEnabled) {
            LOGGER.debug("Kafka publishing disabled; skipping event {}", event.getEventType());
            return;
        }

        kafkaTemplate.send(topicName, event.getFlightNumber(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        LOGGER.warn("Unable to publish flight event {} for {}", event.getEventType(), event.getFlightNumber(), ex);
                    }
                });
    }
}
