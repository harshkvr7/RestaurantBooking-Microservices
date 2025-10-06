package com.rms.restaurant_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic restaurantEventsTopic() {
        return TopicBuilder.name("restaurant-events")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic restaurantViewedEventTopic() {
        return TopicBuilder.name("restaurant-view-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic reviewAddedEventTopic() {
        return TopicBuilder.name("restaurant-review-added-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ratingAddedEventTopic() {
        return TopicBuilder.name("restaurant-rating-added-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }

    
}