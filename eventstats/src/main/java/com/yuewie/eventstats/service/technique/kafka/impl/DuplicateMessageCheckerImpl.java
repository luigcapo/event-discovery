package com.yuewie.eventstats.service.technique.kafka.impl;

import com.yuewie.eventstats.service.technique.kafka.DuplicateMessageChecker;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class DuplicateMessageCheckerImpl implements DuplicateMessageChecker {


    private final StringRedisTemplate redisTemplate;

    public DuplicateMessageCheckerImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Méthode pour ajouter un ou plusieurs membres à un Set.
     * Correspond à la commande Redis SADD.
     * Si le Set n'existe pas, il est créé.
     *
     * @param membres Les valeurs à ajouter au Set.
     */
    public void ajouterAuSet(String keySet, String... membres) {
        if (membres != null && membres.length > 0) {
            // opsForSet() donne accès aux opérations sur les Sets
            redisTemplate.opsForSet().add(keySet, membres);
        }
    }

    public boolean isAlreadyProcessed(String keySet, String deliveryId) {
        // isMember() vérifie l'appartenance d'un élément dans le set
        Boolean appartient = redisTemplate.opsForSet() .isMember(keySet, deliveryId);
        return Boolean.TRUE.equals(appartient);
    }
}
