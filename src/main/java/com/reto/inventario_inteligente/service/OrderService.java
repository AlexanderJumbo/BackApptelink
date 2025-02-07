package com.reto.inventario_inteligente.service;

import com.reto.inventario_inteligente.dto.OrderResponse;
import com.reto.inventario_inteligente.entity.OrderDetail;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(Long clientId, List<OrderDetail> orderDetails);
    Boolean changeOrderStatus(Long orderId);
    Page<OrderResponse> getAllOrders(int numberPage, int numberRecordsPerPage, String orderRecordsByField, String orderRecordsByDirection);
}
