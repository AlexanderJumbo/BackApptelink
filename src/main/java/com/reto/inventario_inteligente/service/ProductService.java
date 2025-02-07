package com.reto.inventario_inteligente.service;

import com.reto.inventario_inteligente.dto.ProductRequest;
import com.reto.inventario_inteligente.dto.ProductResponse;
import com.reto.inventario_inteligente.entity.Product;
import org.springframework.data.domain.Page;

public interface ProductService {

    ProductResponse createProduct(ProductRequest productRequest);
    Page<ProductResponse> getAllProducts(int numberPage, int numberRecordsPerPage, String orderRecordsByField, String orderRecordsByDirection);
    ProductResponse getProductById(Long productId);
    ProductResponse updateProduct(ProductRequest productRequest);
    Boolean deleteProduct(Long productId);
}
