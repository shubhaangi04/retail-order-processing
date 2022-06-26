package com.retail.orderprocessing.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.retail.orderprocessing.model.Product;
import lombok.*;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDTO {

  private String orderId;

  private String orderStatus;

  @NotBlank(message = "Customer ID is mandatory")
  private String customerId;

  @NotEmpty(message = "Product Details cannot be empty")
  private List<Product> products;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss z")
  private Date orderTime;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss z")
  private Date lastUpdatedTime;

  @DecimalMin(value = "0.0", message = "Total price should be grater than or equal to 0")
  private double totalPrice;
}
