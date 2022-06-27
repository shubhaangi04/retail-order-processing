package com.retail.orderprocessing.service.impl;

import com.retail.orderprocessing.dao.OrderProcessingRepository;
import com.retail.orderprocessing.kafka.ProducerService;
import com.retail.orderprocessing.model.dto.OrderDTO;
import com.retail.orderprocessing.model.entity.Order;
import com.retail.orderprocessing.model.Product;
import com.retail.orderprocessing.model.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderProcessingServiceImplTest {

  @Mock OrderProcessingRepository mockOrderProcessingRepository;
  @Mock ProducerService mockProducerService;
  @Mock ModelMapper modelMapper;

  @InjectMocks OrderProcessingServiceImpl orderProcessingService;

  private Order order;
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
    order =
        new Order(
            "1",
            OrderStatus.PLACED.toString(),
            "Customer 1",
            productList,
            orderDate,
            orderDate,
            150.0f);
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

  @DisplayName("Test order create")
  @Test
  public void testCreateOrder() {
    given(mockOrderProcessingRepository.save(Mockito.any())).willReturn(order);
    willDoNothing()
        .given(mockProducerService)
        .sendOrder(Mockito.any(), Mockito.any(), Mockito.any());
    given(modelMapper.map(Mockito.isA(OrderDTO.class), Mockito.any())).willReturn(order);
    given(modelMapper.map(Mockito.isA(Order.class), Mockito.any())).willReturn(orderDTO);

    OrderDTO actualOrderDTO = orderProcessingService.createOrder(orderDTO);
    verify(mockProducerService, times(1)).sendOrder(Mockito.any(), Mockito.any(), Mockito.any());
    verify(mockOrderProcessingRepository, times(1)).save(Mockito.any());
    assertThat(actualOrderDTO.getOrderId()).isEqualTo("1");
    assertThat(actualOrderDTO.getOrderStatus()).isEqualTo(OrderStatus.PLACED.toString());
    assertThat(actualOrderDTO.getOrderTime()).isEqualTo(orderDate);
    assertThat(actualOrderDTO.getCustomerId()).isEqualTo("Customer 1");
    assertThat(actualOrderDTO.getTotalPrice()).isEqualTo(150.0f);
    assertThat(actualOrderDTO.getProducts().size()).isEqualTo(5);
  }

  @DisplayName("Test Get order by id for order exists")
  @Test
  public void testGetOrderDetailsByOrderId() {
    given(mockOrderProcessingRepository.findById(Mockito.anyString()))
        .willReturn(Optional.of(order));
    given(modelMapper.map(Mockito.any(), Mockito.any())).willReturn(orderDTO);

    OrderDTO actualOrderDTO = orderProcessingService.getOrderDetailsByOrderId("Some Id");
    verify(mockOrderProcessingRepository, times(1)).findById(Mockito.anyString());
    assertThat(actualOrderDTO.getOrderId()).isEqualTo("1");
    assertThat(actualOrderDTO.getOrderStatus()).isEqualTo(OrderStatus.PLACED.toString());
    assertThat(actualOrderDTO.getOrderTime()).isEqualTo(orderDate);
    assertThat(actualOrderDTO.getCustomerId()).isEqualTo("Customer 1");
    assertThat(actualOrderDTO.getTotalPrice()).isEqualTo(150.0f);
    assertThat(actualOrderDTO.getProducts().size()).isEqualTo(5);
  }

  @DisplayName("Test Get order by id for order does not exist")
  @Test
  public void testGetOrderDetailsByOrderIdWhenOrderDoesNotExist() {
    given(mockOrderProcessingRepository.findById(Mockito.anyString())).willReturn(Optional.empty());
    assertThrows(
        ResponseStatusException.class,
        () -> orderProcessingService.getOrderDetailsByOrderId("Some Id"));
    verify(mockOrderProcessingRepository, times(1)).findById(Mockito.anyString());
  }

  @DisplayName("Test Get all orders for non-empty data")
  @Test
  public void testGetAllOrders() {
    given(modelMapper.map(Mockito.isA(Order.class), Mockito.any())).willReturn(orderDTO);
    List<Order> orderList = new ArrayList<>();
    orderList.add(order);
    given(mockOrderProcessingRepository.findAll()).willReturn(orderList);
    List<OrderDTO> orderDTOList = orderProcessingService.getAllOrders();
    verify(mockOrderProcessingRepository, times(1)).findAll();
    assertThat(orderDTOList.get(0).getOrderId()).isEqualTo("1");
    assertThat(orderDTOList.get(0).getOrderStatus()).isEqualTo(OrderStatus.PLACED.toString());
    assertThat(orderDTOList.get(0).getOrderTime()).isEqualTo(orderDate);
    assertThat(orderDTOList.get(0).getCustomerId()).isEqualTo("Customer 1");
    assertThat(orderDTOList.get(0).getTotalPrice()).isEqualTo(150.0f);
    assertThat(orderDTOList.get(0).getProducts().size()).isEqualTo(5);
  }

  @DisplayName("Test Get all orders for empty data")
  @Test
  public void testGetAllOrdersWhenNoOrdersExist() {
    List<Order> orderList = new ArrayList<>();
    given(mockOrderProcessingRepository.findAll()).willReturn(orderList);
    List<OrderDTO> orderDTOList = orderProcessingService.getAllOrders();
    verify(mockOrderProcessingRepository, times(1)).findAll();
    assertTrue(orderDTOList.isEmpty());
  }

  @DisplayName("Test update order status")
  @Test
  public void testUpdateOrderStatus() {
    given(mockOrderProcessingRepository.findById(Mockito.anyString()))
        .willReturn(Optional.of(order));
    given(mockOrderProcessingRepository.save(Mockito.any())).willReturn(order);
    orderProcessingService.updateOrderStatus(
        "{\"orderId\":\"37900e79-e5fd-4cfc-b6ce-7ecadcacdf6b\",\"orderStatus\":\"PLACED\",\"customerId\":\"new123\",\"products\":[{\"id\":\"ig96du\",\"name\":\"pen\",\"price\":120.0}],\"orderTime\":\"2022-06-25 13:10:27 UTC\",\"totalPrice\":120.0}");
    verify(mockOrderProcessingRepository, times(1)).findById(Mockito.anyString());
    verify(mockOrderProcessingRepository, times(1)).save(Mockito.any());
  }

  @DisplayName("Test update order status with Json Processing Exception")
  @Test
  public void testUpdateOrderStatusWithJsonProcessingError() {
    orderProcessingService.updateOrderStatus(
        "\"orderId\":\"37900e79-e5fd-4cfc-b6ce-7ecadcacdf6b\",\"orderStatus\":\"PLACED\",\"customerId\":\"new123\",\"products\":[{\"id\":\"ig96du\",\"name\":\"pen\",\"price\":120.0}],\"orderTime\":\"2022-06-25 13:10:27 UTC\",\"totalPrice\":120.0}");
    verify(mockOrderProcessingRepository, times(0)).findById(Mockito.anyString());
    verify(mockOrderProcessingRepository, times(0)).save(Mockito.any());
  }

  @DisplayName("Test update order status with id not found")
  @Test
  public void testUpdateOrderStatusWithIdNotFound() {
    given(mockOrderProcessingRepository.findById(Mockito.anyString())).willReturn(Optional.empty());
    orderProcessingService.updateOrderStatus(
        "{\"orderId\":\"37900e79-e5fd-4cfc-b6ce-7ecadcacdf6b\",\"orderStatus\":\"PLACED\",\"customerId\":\"new123\",\"products\":[{\"id\":\"ig96du\",\"name\":\"pen\",\"price\":120.0}],\"orderTime\":\"2022-06-25 13:10:27 UTC\",\"totalPrice\":120.0}");
    verify(mockOrderProcessingRepository, times(1)).findById(Mockito.anyString());
    verify(mockOrderProcessingRepository, times(0)).save(Mockito.any());
  }
}
