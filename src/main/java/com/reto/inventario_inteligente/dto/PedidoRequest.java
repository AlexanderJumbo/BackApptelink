package com.reto.inventario_inteligente.dto;

import com.reto.inventario_inteligente.entity.OrderDetail;

import java.io.Serializable;
import java.util.List;

public class PedidoRequest implements Serializable {

    private Long clientId;
    private List<OrderDetail> orderDetails;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
