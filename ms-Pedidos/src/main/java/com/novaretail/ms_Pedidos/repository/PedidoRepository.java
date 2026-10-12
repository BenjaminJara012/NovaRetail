package com.novaretail.ms_Pedidos.repository;

import com.novaretail.ms_Pedidos.model.PedidoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface PedidoRepository extends JpaRepository<PedidoModel, Long> {

}
