package com.mealstream.order.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

  @Bean
  public NewTopic orderCreatedTopic() {
    return TopicBuilder.name("order-created")
        .partitions(6)
        .replicas(1) // 1 for local dev; use 3 in production
        .build();
  }

  @Bean
  public NewTopic orderCreatedDlt() {
    return TopicBuilder.name("order-created.DLT").partitions(3).replicas(1).build();
  }

  @Bean
  public NewTopic orderAcceptedTopic() {
    return TopicBuilder.name("order-accepted").partitions(6).replicas(1).build();
  }

  @Bean
  public NewTopic orderAcceptedDlt() {
    return TopicBuilder.name("order-accepted.DLT").partitions(3).replicas(1).build();
  }

  @Bean
  public NewTopic orderRejectedTopic() {
    return TopicBuilder.name("order-rejected").partitions(6).replicas(1).build();
  }

  @Bean
  public NewTopic orderRejectedDlt() {
    return TopicBuilder.name("order-rejected.DLT").partitions(3).replicas(1).build();
  }

  @Bean
  public NewTopic deliveryAssignedTopic() {
    return TopicBuilder.name("delivery-assigned").partitions(6).replicas(1).build();
  }

  @Bean
  public NewTopic deliveryAssignedDlt() {
    return TopicBuilder.name("delivery-assigned.DLT").partitions(3).replicas(1).build();
  }

  @Bean
  public NewTopic deliveryFailedTopic() {
    return TopicBuilder.name("delivery-failed").partitions(3).replicas(1).build();
  }

  @Bean
  public NewTopic deliveryFailedDlt() {
    return TopicBuilder.name("delivery-failed.DLT").partitions(3).replicas(1).build();
  }
}
