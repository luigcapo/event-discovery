package com.yuewie.eventnotifications.config;

import com.yuewie.eventnotifications.dto.EventCreatedDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

@Configuration
public class KafkaConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setRecordMessageConverter(new StringJsonMessageConverter());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        //factory.setCommonErrorHandler(errorHandler); // Set the error handler
        return factory;
    }

    /*@Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> template) {
        // After 2 failed retries, publish to a dead-letter topic
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template,
                (record, ex) -> new TopicPartition(record.topic() + ".DLT", -1));

        // Retry twice with a 1-second delay
        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 2));
    }*/
}