package com.retail.orderprocessing.service;

import com.retail.orderprocessing.model.dto.OrderDTO;

import java.util.List;

public interface OrderProcessingService {
  OrderDTO createOrder(OrderDTO orderDto);

  OrderDTO getOrderDetailsByOrderId(String orderId);

  List<OrderDTO> getAllOrders();

  void updateOrderStatus(String orderDetails);
}
