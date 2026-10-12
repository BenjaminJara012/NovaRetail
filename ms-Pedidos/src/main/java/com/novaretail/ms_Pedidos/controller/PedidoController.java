package com.novaretail.ms_Pedidos.controller;

import com.novaretail.ms_Pedidos.model.PedidoModel;
import com.novaretail.ms_Pedidos.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")

public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public List<PedidoModel> obtenerPedidos() {
        return pedidoService.obtenerPedidos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoModel> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<PedidoModel> crearPedido(
            @Valid @RequestBody PedidoModel pedido) {

        PedidoModel nuevo = pedidoService.crearPedido(pedido);
        return ResponseEntity.status(201).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoModel> actualizarPedido(
            @PathVariable Long id,
            @Valid @RequestBody PedidoModel datos) {

        PedidoModel actualizado = pedidoService.actualizarPedido(id, datos);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        pedidoService.eliminarPedido(id);
        return ResponseEntity.noContent().build();
    }
}
