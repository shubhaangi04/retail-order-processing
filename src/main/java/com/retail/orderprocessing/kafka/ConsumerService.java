package com.retail.orderprocessing.kafka;

import com.retail.orderprocessing.service.OrderProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

  private final OrderProcessingService orderProcessingService;

  @Autowired
  public ConsumerService(OrderProcessingService orderProcessingService) {
    this.orderProcessingService = orderProcessingService;
  }

  @KafkaListener(topics = "${app.kafka.topic_name}", groupId = "order_consumer")
  public void getOrderDetailsForProcessing(String orderDetails) {
    orderProcessingService.updateOrderStatus(orderDetails);
  }
}
