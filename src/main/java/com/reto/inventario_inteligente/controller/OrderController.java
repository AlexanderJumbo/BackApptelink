package com.reto.inventario_inteligente.controller;

import com.reto.inventario_inteligente.dto.OrderResponse;
import com.reto.inventario_inteligente.dto.PedidoRequest;
import com.reto.inventario_inteligente.service.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderServiceImpl orderService;
    @PostMapping
    public ResponseEntity<OrderResponse> crearPedido(@RequestBody PedidoRequest pedidoRequest) {
        OrderResponse order = orderService.createOrder(pedidoRequest.getClientId(), pedidoRequest.getOrderDetails());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
    @PutMapping("/change-status/{id}")
    public ResponseEntity<Boolean> changeStatusOrder(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.changeOrderStatus(id));
    }

    @GetMapping("/all")
    public Page<OrderResponse> getAllOrders(
            @RequestParam(value = "p", defaultValue = "", required = false) int numberPage,
            @RequestParam(value = "n", defaultValue = "", required = false) int numberRecordsPerPage,
            @RequestParam(value = "f", defaultValue = "id", required = false) String orderRecordsByField,
            @RequestParam(value = "d", defaultValue = "asc", required = false) String orderRecordsByDirection
    ){
        return orderService.getAllOrders(numberPage, numberRecordsPerPage, orderRecordsByField, orderRecordsByDirection);
    }
}
