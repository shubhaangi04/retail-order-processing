package com.retail.orderprocessing.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.retail.orderprocessing.model.Product;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "orderDetails")
public class Order {
  @Id private String orderId;

  private String orderStatus;

  private String customerId;

  private List<Product> products;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss z")
  private Date orderTime;

  private double totalPrice;
}
