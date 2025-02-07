package com.reto.inventario_inteligente.service;

import com.reto.inventario_inteligente.dto.ControlTableRequest;
import com.reto.inventario_inteligente.entity.ControlTable;
import com.reto.inventario_inteligente.entity.Product;
import com.reto.inventario_inteligente.exception.ObjectNotFoundException;
import com.reto.inventario_inteligente.repository.ControlTableRepository;
import com.reto.inventario_inteligente.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ControlTableImpl implements ControlTableService{
    @Autowired
    private ControlTableRepository controlRepository;
    @Autowired
    private ProductRepository productRepository;
    @Override
    public ControlTable registerMovement(ControlTableRequest controlTable) {

        Product productFound = productRepository.findById(controlTable.getProduct())
                .orElseThrow(() -> new ObjectNotFoundException("Product no found"));

        ControlTable newMovement = new ControlTable();
        newMovement.setCategory(controlTable.getCategory());
        newMovement.setOperationDate(LocalDate.now());
        newMovement.setOperationType(controlTable.getType());
        newMovement.setProduct(productFound);
        newMovement.setQuantity(controlTable.getQuantity());
        newMovement.setUnitPrice(controlTable.getPrice());
        newMovement.setTotal(controlTable.getTotal());
        newMovement.setDescription(controlTable.getDescription());
        return controlRepository.save(newMovement);
    }
}
