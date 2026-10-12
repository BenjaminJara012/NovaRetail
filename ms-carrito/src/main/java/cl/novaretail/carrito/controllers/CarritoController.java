package cl.novaretail.carrito.controllers;

import cl.novaretail.carrito.dtos.ActualizarCantidadRequestDTO;
import cl.novaretail.carrito.dtos.AgregarItemRequestDTO;
import cl.novaretail.carrito.dtos.CarritoResponseDTO;
import cl.novaretail.carrito.services.CarritoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carritos")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping("/{usuarioId}")
    public ResponseEntity<CarritoResponseDTO> obtenerCarrito(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                carritoService.obtenerCarrito(usuarioId)
        );
    }

    @PostMapping("/{usuarioId}/items")
    public ResponseEntity<CarritoResponseDTO> agregarItem(
            @PathVariable Long usuarioId,
            @Valid @RequestBody AgregarItemRequestDTO dto) {

        return ResponseEntity.ok(
                carritoService.agregarItem(usuarioId, dto)
        );
    }

    @DeleteMapping("/{usuarioId}/items/{itemId}")
    public ResponseEntity<CarritoResponseDTO> eliminarItem(
            @PathVariable Long usuarioId,
            @PathVariable Long itemId) {

        return ResponseEntity.ok(
                carritoService.eliminarItem(usuarioId, itemId)
        );
    }

    @PutMapping("/{usuarioId}/items/{itemId}")
    public ResponseEntity<CarritoResponseDTO> actualizarCantidad(
            @PathVariable Long usuarioId,
            @PathVariable Long itemId,
            @Valid @RequestBody ActualizarCantidadRequestDTO dto) {

        return ResponseEntity.ok(
                carritoService.actualizarCantidad(usuarioId, itemId, dto)
        );
    }

    @DeleteMapping("/{usuarioId}/items")
    public ResponseEntity<CarritoResponseDTO> vaciarCarrito(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                carritoService.vaciarCarrito(usuarioId)
        );
    }
}
