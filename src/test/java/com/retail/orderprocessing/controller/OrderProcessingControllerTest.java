package com.retail.orderprocessing.controller;

import com.retail.orderprocessing.model.Product;
import com.retail.orderprocessing.model.dto.OrderDTO;
import com.retail.orderprocessing.model.enums.OrderStatus;
import com.retail.orderprocessing.service.impl.OrderProcessingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OrderProcessingControllerTest {

  @Mock OrderProcessingServiceImpl orderProcessingService;
  @InjectMocks OrderProcessingController orderProcessingController;

  private OrderDTO orderDTO;
  private Date orderDate;

  @BeforeEach
  public void setup() {
    List<Product> productList = new ArrayList<>();
    productList.add(new Product("1", "Product 1", 10.0f));
    productList.add(new Product("2", "Product 2", 20.0f));
    productList.add(new Product("3", "Product 3", 30.0f));
    productList.add(new Product("4", "Product 4", 40.0f));
    productList.add(new Product("5", "Product 5", 50.0f));
    orderDate = new Date();
    orderDTO =
        new OrderDTO(
            "1",
            OrderStatus.PLACED.toString(),
            "Customer 1",
            productList,
            orderDate,
            orderDate,
            150.0f);
  }

  @DisplayName("Test place order")
  @Test
  public void testPlaceOrder() {
    given(orderProcessingService.createOrder(Mockito.any())).willReturn(orderDTO);
    ResponseEntity<OrderDTO> response = orderProcessingController.placeOrder(orderDTO);
    verify(orderProcessingService, times(1)).createOrder(Mockito.any());
    assertNotNull(response);
    assertNotNull(response.getBody());
    OrderDTO actualOrder = response.getBody();
    assertThat(actualOrder.getOrderId()).isEqualTo("1");
    assertThat(actualOrder.getOrderStatus()).isEqualTo(OrderStatus.PLACED.toString());
    assertThat(actualOrder.getOrderTime()).isEqualTo(orderDate);
    assertThat(actualOrder.getCustomerId()).isEqualTo("Customer 1");
    assertThat(actualOrder.getTotalPrice()).isEqualTo(150.0f);
    assertThat(actualOrder.getProducts().size()).isEqualTo(5);
  }

  @DisplayName("Test get all orders")
  @Test
  public void testGetAllOrders() {
    List<OrderDTO> orderDTOList = new ArrayList<>();
    orderDTOList.add(orderDTO);
    given(orderProcessingService.getAllOrders()).willReturn(orderDTOList);
    ResponseEntity<List<OrderDTO>> response = orderProcessingController.getAllOrders();
    verify(orderProcessingService, times(1)).getAllOrders();
    assertNotNull(response);
    assertNotNull(response.getBody());
    OrderDTO actualOrder = response.getBody().get(0);
    assertThat(actualOrder.getOrderId()).isEqualTo("1");
    assertThat(actualOrder.getOrderStatus()).isEqualTo(OrderStatus.PLACED.toString());
    assertThat(actualOrder.getOrderTime()).isEqualTo(orderDate);
    assertThat(actualOrder.getCustomerId()).isEqualTo("Customer 1");
    assertThat(actualOrder.getTotalPrice()).isEqualTo(150.0f);
    assertThat(actualOrder.getProducts().size()).isEqualTo(5);
  }

  @DisplayName("Test get order by id")
  @Test
  public void testGetOrderByOrderId() {
    given(orderProcessingService.getOrderDetailsByOrderId(Mockito.any())).willReturn(orderDTO);
    ResponseEntity<OrderDTO> response =
        orderProcessingController.getOrderByOrderId(orderDTO.getOrderId());
    verify(orderProcessingService, times(1)).getOrderDetailsByOrderId(Mockito.any());
    assertNotNull(response);
    assertNotNull(response.getBody());
    OrderDTO actualOrder = response.getBody();
    assertThat(actualOrder.getOrderId()).isEqualTo("1");
    assertThat(actualOrder.getOrderStatus()).isEqualTo(OrderStatus.PLACED.toString());
    assertThat(actualOrder.getOrderTime()).isEqualTo(orderDate);
    assertThat(actualOrder.getCustomerId()).isEqualTo("Customer 1");
    assertThat(actualOrder.getTotalPrice()).isEqualTo(150.0f);
    assertThat(actualOrder.getProducts().size()).isEqualTo(5);
  }
}
