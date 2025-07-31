package com.yuewie.eventstats.service.technique.kafka;

public interface DuplicateMessageChecker {
    boolean isAlreadyProcessed(String keySet, String deliveryId);
    void ajouterAuSet(String keySet, String... membres);
}
