package com.reto.inventario_inteligente.service;

import com.reto.inventario_inteligente.dto.ControlTableRequest;
import com.reto.inventario_inteligente.entity.ControlTable;

public interface ControlTableService {

    ControlTable registerMovement(ControlTableRequest controlTableRequest);

}
