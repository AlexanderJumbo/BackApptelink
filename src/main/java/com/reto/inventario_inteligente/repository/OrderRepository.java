package com.reto.inventario_inteligente.repository;

import com.reto.inventario_inteligente.dto.OrderResponse;
import com.reto.inventario_inteligente.entity.Order;
import com.reto.inventario_inteligente.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("select o from Order o where o.status = true")
    Page<Order> getAllOrders(Pageable pageable);
}
