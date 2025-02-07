package com.reto.inventario_inteligente.service;

import com.reto.inventario_inteligente.dto.ControlTableRequest;
import com.reto.inventario_inteligente.dto.ProductRequest;
import com.reto.inventario_inteligente.dto.ProductResponse;
import com.reto.inventario_inteligente.entity.ControlTable;
import com.reto.inventario_inteligente.entity.Product;
import com.reto.inventario_inteligente.exception.ObjectNotFoundException;
import com.reto.inventario_inteligente.exception.RequestException;
import com.reto.inventario_inteligente.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ControlTableService controlTableService;
    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {

        if(productRequest.getNameProduct().isEmpty() || productRequest.getNameProduct() == null){
            System.out.println("PRODUCT NAME EMPTY");
            throw new RequestException(400, "PRODUCT NAME DON'T BE EMPTY");
        }
        if(productRequest.getPrice() <= 0){
            System.out.println("PRODUCT PRICE <= 0");
            throw new RequestException(400, "PRODUCT PRICE DON'T BE <= 0");
        }
        if(productRequest.getStock() <= 0){
            System.out.println("PRODUCT STOCK <=0");
            throw new RequestException(400, "PRODUCT STOCK DON'T BE <= 0");
        }

        Product newProduct = new Product();
        newProduct.setName(productRequest.getNameProduct());
        newProduct.setDescription(productRequest.getDescription());
        newProduct.setPrice(productRequest.getPrice());
        newProduct.setStockQuantity(productRequest.getStock());
        newProduct.setStatus(productRequest.isStatus());

        Product pro = productRepository.save(newProduct);
        if(pro != null){
            ControlTableRequest controlRequest = new ControlTableRequest("PRODUCTO", "ALTA", newProduct.getId(),
                    newProduct.getStockQuantity(), BigDecimal.valueOf(newProduct.getPrice()), BigDecimal.valueOf(newProduct.getPrice() * newProduct.getStockQuantity()), "INGRESO DE PRODUCTO");
            ControlTable newMovement =  controlTableService.registerMovement(controlRequest);
        }
        return mapDTO(productRepository.save(newProduct));
    }

    @Override
    public Page<ProductResponse> getAllProducts(int numberPage, int numberRecordsPerPage, String orderRecordsByField, String orderRecordsByDirection) {
        Sort sort = orderRecordsByDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(orderRecordsByField).ascending() : Sort.by(orderRecordsByField).descending();
        Pageable pageable = PageRequest.of(numberPage, numberRecordsPerPage, sort);
        //System.out.println("GET ALL: " + productRepository.getAllProducts(pageable));
        return productRepository.getAllProducts(pageable).map(this::mapDTO); //productRepository.findAll(pageable).map(this::mapDTO);
    }
    @Override
    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ObjectNotFoundException("Product no found: " + productId));
        return mapDTO(product);
    }

    @Override
    public ProductResponse updateProduct(ProductRequest productRequest) {

        Product productFound = productRepository.findById(productRequest.getProductId())
                .orElseThrow(() -> new ObjectNotFoundException("Product no found: " + productRequest.getProductId()));

        productFound.setName(productRequest.getNameProduct());
        productFound.setDescription(productRequest.getDescription());
        productFound.setPrice(productRequest.getPrice());
        productFound.setStockQuantity(productRequest.getStock());
        productFound.setStatus(productRequest.isStatus());

        return mapDTO(productRepository.save(productFound));
    }

    @Override
    public Boolean deleteProduct(Long productId) {

        Product productFound = productRepository.findById(productId)
                .orElseThrow(() -> new ObjectNotFoundException("Product no found: " + productId));
        productFound.setStatus(false);
        return !productRepository.save(productFound).isStatus();
    }

    private ProductResponse mapDTO(Product product) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setProductId(product.getId());
        productResponse.setProductName(product.getName());
        productResponse.setProductDesc(product.getDescription());
        productResponse.setProductPrice(product.getPrice());
        productResponse.setQuantity(product.getStockQuantity());
        productResponse.setStatus(product.isStatus());
        return productResponse;
    }
}
