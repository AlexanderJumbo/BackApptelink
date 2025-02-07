package com.reto.inventario_inteligente.controller;

import com.reto.inventario_inteligente.dto.ProductRequest;
import com.reto.inventario_inteligente.dto.ProductResponse;
import com.reto.inventario_inteligente.entity.Product;
import com.reto.inventario_inteligente.service.ProductService;
import com.reto.inventario_inteligente.service.ReportService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productRequest));
    }

    @GetMapping("/all")
    public Page<ProductResponse> getAllProducts(
            @RequestParam(name = "p", defaultValue = "", required = false) int numberPage,
            @RequestParam(name = "n", defaultValue = "10", required = false) int numberRecordsPerPage,
            @RequestParam(name = "f", defaultValue = "id", required = false) String orderRecordsByField,
            @RequestParam(name = "d", defaultValue = "asc", required = false) String orderRecordsByDirection
    ){
        return productService.getAllProducts(numberPage, numberRecordsPerPage, orderRecordsByField, orderRecordsByDirection);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductById(id));
    }

    @PutMapping("/update")
    public ResponseEntity<ProductResponse> updateProduct(@RequestBody ProductRequest productRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.updateProduct(productRequest));
    }
    @PutMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.deleteProduct(id));
    }

}
