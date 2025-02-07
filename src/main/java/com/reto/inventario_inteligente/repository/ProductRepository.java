package com.reto.inventario_inteligente.repository;

import com.reto.inventario_inteligente.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select sum(price * stockQuantity) from Product")
    Double getTotalInventory();
    @Query("select avg(price) from Product")
    Double getAVGInventory();
    @Query("select p from Product p where p.status = true")
    Page<Product> getAllProducts(Pageable pageable);

}
