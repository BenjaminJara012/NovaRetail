
package com.novaretail.ms_Pedidos.service;

import com.novaretail.ms_Pedidos.model.PedidoModel;
import com.novaretail.ms_Pedidos.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@Service

public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public List<PedidoModel> obtenerPedidos() {
        return pedidoRepository.findAll();
    }

    public PedidoModel buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }

    public PedidoModel crearPedido(PedidoModel pedido) {
        pedido.setId(null);
        pedido.setEstado("PENDIENTE");
        pedido.setFechaPedido(LocalDateTime.now());

        return pedidoRepository.save(pedido);
    }

    public PedidoModel actualizarPedido(Long id, PedidoModel datos) {
        PedidoModel pedido = buscarPorId(id);

        pedido.setUsuarioId(datos.getUsuarioId());
        pedido.setTotal(datos.getTotal());

        return pedidoRepository.save(pedido);
    }

    public void eliminarPedido(Long id) {
        PedidoModel pedido = buscarPorId(id);
        pedidoRepository.delete(pedido);
    }
}
