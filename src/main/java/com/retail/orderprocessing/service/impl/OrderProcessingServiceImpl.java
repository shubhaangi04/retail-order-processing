package com.retail.orderprocessing.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.retail.orderprocessing.dao.OrderProcessingRepository;
import com.retail.orderprocessing.kafka.ProducerService;
import com.retail.orderprocessing.model.dto.OrderDTO;
import com.retail.orderprocessing.model.entity.Order;
import com.retail.orderprocessing.model.enums.OrderStatus;
import com.retail.orderprocessing.service.OrderProcessingService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderProcessingServiceImpl implements OrderProcessingService {

  @Value("${app.kafka.topic_name}")
  private String topicName;

  private final Logger logger = LoggerFactory.getLogger(OrderProcessingServiceImpl.class);

  private final OrderProcessingRepository orderProcessingRepository;
  private final ProducerService producerService;

  private final ModelMapper modelMapper;

  @Autowired
  public OrderProcessingServiceImpl(
      ModelMapper modelMapper,
      OrderProcessingRepository orderProcessingRepository,
      ProducerService producerService) {
    this.modelMapper = modelMapper;
    this.orderProcessingRepository = orderProcessingRepository;
    this.producerService = producerService;
  }

  @Override
  public OrderDTO createOrder(OrderDTO orderDto) {
    Order order = modelMapper.map(orderDto, Order.class);
    order.setOrderId(UUID.randomUUID().toString());
    order.setOrderStatus(OrderStatus.PLACED.toString());
    order.setOrderTime(new Date());
    Order savedOrder = orderProcessingRepository.save(order);
    try {
      producerService.sendOrder(
          topicName,
          UUID.randomUUID().toString(),
          new ObjectMapper().writeValueAsString(savedOrder));
    } catch (JsonProcessingException e) {
      logger.error("Unable to parse order details", e);
    }
    return modelMapper.map(savedOrder, OrderDTO.class);
  }

  @Override
  public OrderDTO getOrderDetailsByOrderId(String orderId) {
    if (!orderProcessingRepository.existsById(orderId)) {
      logger.error("Order does not exist by id : " + orderId);
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Order does not exist by id : " + orderId);
    }
    return modelMapper.map(orderProcessingRepository.findById(orderId), OrderDTO.class);
  }

  @Override
  public List<OrderDTO> getAllOrders() {
    return orderProcessingRepository.findAll().stream()
        .map(order -> modelMapper.map(order, OrderDTO.class))
        .collect(Collectors.toList());
  }

  @Override
  public void updateOrderStatus(String orderDetails) {
    Order orderData;
    try {
      orderData = new ObjectMapper().readValue(orderDetails, Order.class);
      Optional<Order> optionalOrderDetails =
          orderProcessingRepository.findById(orderData.getOrderId());
      optionalOrderDetails.ifPresentOrElse(
          order -> {
            order.setOrderStatus(OrderStatus.PROCESSED.toString());
            orderProcessingRepository.save(order);
          },
          () -> logger.error("Order ID does not exist"));
    } catch (JsonProcessingException e) {
      logger.error("Unable to parse order details", e);
    }
  }
}
