package com.retail.orderprocessing.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.validation.constraints.NotNull;

@Service
public class ProducerService {

  private static final Logger logger = LoggerFactory.getLogger(ProducerService.class);
  private final KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  public ProducerService(KafkaTemplate<String, String> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @Async
  public void sendOrder(String topicName, String key, String message) {
    ListenableFuture<SendResult<String, String>> future =
        kafkaTemplate.send(topicName, key, message);

    future.addCallback(
        new ListenableFutureCallback<>() {

          @Override
          public void onSuccess(SendResult<String, String> result) {}

          @Override
          public void onFailure(@NotNull Throwable e) {
            logger.error("Unable to send Order Details = [" + message + "]", e);
          }
        });
  }
}
