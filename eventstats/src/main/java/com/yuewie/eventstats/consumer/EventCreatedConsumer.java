package com.yuewie.eventstats.consumer;

import com.yuewie.eventnotifications.dto.EventCreatedDto;
import com.yuewie.eventnotifications.service.technique.kafka.DuplicateMessageChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventCreatedConsumer extends BaseKafkaConsumer<EventCreatedDto> {

    public EventCreatedConsumer(DuplicateMessageChecker duplicateChecker) {
        super(duplicateChecker);
    }

    @KafkaListener(topics = "${app.kafka.topic.eventCreated}", groupId = "${app.kafka.group.id.events}")
    public void listen(@Payload EventCreatedDto data,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                       @Header(KafkaHeaders.OFFSET) long offset,
                       Acknowledgment ack) {
        // On délègue toute la logique au template de la classe de base
        super.executeProcessingFlow(data, topic, partition, offset, ack);
    }

    @Override
    protected void handlePayload(EventCreatedDto payload, int partition, long offset) {
        log.info("Received message [{}] from group1, partition-{} with offset-{}",
                payload,
                partition,
                offset);
    }
}
