package com.reto.inventario_inteligente.repository;

import com.reto.inventario_inteligente.entity.ControlTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ControlTableRepository extends JpaRepository<ControlTable, Long> {
}
