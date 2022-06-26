package com.retail.orderprocessing.controller;

import com.retail.orderprocessing.model.dto.OrderDTO;
import com.retail.orderprocessing.service.OrderProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/orders")
public class OrderProcessingController {

  private final OrderProcessingService orderProcessingService;

  @Autowired
  public OrderProcessingController(OrderProcessingService orderProcessingService) {
    this.orderProcessingService = orderProcessingService;
  }

  @PostMapping
  public ResponseEntity<OrderDTO> placeOrder(@Valid @RequestBody OrderDTO orderDto) {
    return new ResponseEntity<>(orderProcessingService.createOrder(orderDto), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<OrderDTO>> getAllOrders() {
    List<OrderDTO> orders = orderProcessingService.getAllOrders();
    return new ResponseEntity<>(orders, HttpStatus.OK);
  }

  @GetMapping("/{orderId}")
  public ResponseEntity<OrderDTO> getOrderByOrderId(@PathVariable String orderId) {
    return new ResponseEntity<>(
        orderProcessingService.getOrderDetailsByOrderId(orderId), HttpStatus.OK);
  }
}
