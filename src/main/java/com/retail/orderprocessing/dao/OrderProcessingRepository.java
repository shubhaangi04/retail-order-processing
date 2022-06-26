package com.retail.orderprocessing.dao;

import com.retail.orderprocessing.model.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderProcessingRepository extends MongoRepository<Order, String> {}
