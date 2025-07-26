package com.yuewie.apievent.batch;

import com.yuewie.apievent.entity.OutboxEvent;
import com.yuewie.apievent.repository.OutboxEventRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OutboxDispatcher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxEventRepository outboxEventRepository;

    public OutboxDispatcher(KafkaTemplate<String, String> kafkaTemplate, OutboxEventRepository outboxEventRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.outboxEventRepository = outboxEventRepository;
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void dispatchEvents() {
        List<OutboxEvent> unsentEvent = outboxEventRepository.findByProcessedFalse();
        for (OutboxEvent e : unsentEvent) {
            kafkaTemplate.send(e.getTopic(), e.getKafkaKey(), e.getPayload());
            e.setProcessed(true);
            e.setProcessedAt(LocalDateTime.now());
        }
        outboxEventRepository.saveAll(unsentEvent);
    }
}
