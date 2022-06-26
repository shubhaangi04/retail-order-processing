package com.retail.orderprocessing.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Product {

  private String id;

  private String name;

  private float price;
}
