package com.reto.inventario_inteligente.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ControlTableRequest implements Serializable {
    private String category;
    private LocalDate date;
    private String type;
    private Long product;
    private int quantity;
    private BigDecimal price;
    private BigDecimal total;
    private String description;

    public ControlTableRequest(String category, String type, Long product, int quantity, BigDecimal price, BigDecimal total, String description) {
        this.category = category;
        //this.date = date;
        this.type = type;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getProduct() {
        return product;
    }

    public void setProduct(Long product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
