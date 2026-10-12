package cl.novaretail.carrito.services;

import cl.novaretail.carrito.exceptions.RecursoNoEncontradoException;
import cl.novaretail.carrito.clients.ProductoClient;
import cl.novaretail.carrito.dtos.*;
import cl.novaretail.carrito.models.Carrito;
import cl.novaretail.carrito.models.ItemCarrito;
import cl.novaretail.carrito.repositories.CarritoRepository;
import cl.novaretail.carrito.repositories.ItemCarritoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarritoService {
        private final CarritoRepository carritoRepository;
        private final ItemCarritoRepository itemCarritoRepository;
        private final ProductoClient productoClient;

        //Obetener el carrito de usuario
        @Transactional(readOnly = true)
        public CarritoResponseDTO obtenerCarrito(Long usuarioId) {

                Carrito carrito = carritoRepository
                        .findByUsuarioId(usuarioId)
                        .orElseThrow(() -> new RecursoNoEncontradoException(
                                "El usuario no tiene un carrito registrado"
                        ));

                return convertirAResponseDTO(carrito);
        }

        //Sumar producto a carrito
        @Transactional
        public CarritoResponseDTO agregarItem(
                Long usuarioId,
                AgregarItemRequestDTO dto) {

                //Consultar producto en ms-producto
                ProductoDTO producto =
                        productoClient.obtenerProducto(dto.getProductoId());

                //Busca o crea el carrito de usuario
                Carrito carrito = carritoRepository
                        .findByUsuarioId(usuarioId)
                        .orElseGet(() -> {
                        Carrito nuevo = new Carrito();
                        nuevo.setUsuarioId(usuarioId);
                        return carritoRepository.save(nuevo);
                        });

                //Valida si el producto ya está en carrito
                ItemCarrito item = itemCarritoRepository
                        .findByCarritoIdAndProductoId(
                                carrito.getId(),
                                dto.getProductoId()
                        )
                        .orElse(null);

                //Calcular total
                int cantidadActual = item == null ? 0 : item.getCantidad();
                int cantidadNueva = Math.addExact(
                        cantidadActual,
                        dto.getCantidad()
                );

                //Validar stock disponible
                if (producto.getStock() == null ||
                        cantidadNueva > producto.getStock()) {

                throw new IllegalArgumentException(
                        "Stock insuficiente para el producto "
                        + dto.getProductoId()
                );
                }

                //Crea o actualiza item
                if (item == null) {
                item = new ItemCarrito();
                item.setCarrito(carrito);
                item.setProductoId(dto.getProductoId());
                }

                item.setCantidad(cantidadNueva);
                item.setPrecioUnitario(producto.getPrecio());

                itemCarritoRepository.save(item);

                //Devuelve el carrito actualizado
                return convertirAResponseDTO(carrito);
        }

        //Convertir entidad a dto
        private CarritoResponseDTO convertirAResponseDTO(Carrito carrito) {

                List<ItemCarrito> items =
                itemCarritoRepository.findByCarritoId(carrito.getId());

                List<ItemCarritoResponseDTO> itemsDTO =
                        items.stream()
                                .map(this::convertirItemAResponseDTO)
                                .toList();

                BigDecimal total = itemsDTO.stream()
                        .map(ItemCarritoResponseDTO::getSubtotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                CarritoResponseDTO dto = new CarritoResponseDTO();
                dto.setId(carrito.getId());
                dto.setUsuarioId(carrito.getUsuarioId());
                dto.setItems(itemsDTO);
                dto.setTotal(total);

                return dto;
        }

        //Convertir ítem a dto
        private ItemCarritoResponseDTO convertirItemAResponseDTO(
                ItemCarrito item) {

                ItemCarritoResponseDTO dto = new ItemCarritoResponseDTO();

                dto.setId(item.getId());
                dto.setProductoId(item.getProductoId());
                dto.setCantidad(item.getCantidad());
                dto.setPrecioUnitario(item.getPrecioUnitario());

                BigDecimal subtotal = item.getPrecioUnitario()
                        .multiply(BigDecimal.valueOf(item.getCantidad()));

                dto.setSubtotal(subtotal);

                return dto;

        }

        @Transactional
        public CarritoResponseDTO eliminarItem(
                Long usuarioId,
                Long itemId) {

        Carrito carrito = carritoRepository
                .findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El usuario no tiene un carrito registrado"
                ));

        ItemCarrito item = itemCarritoRepository
                .findById(itemId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El ítem con ID " + itemId + " no existe"
                ));

        if (!item.getCarrito().getId().equals(carrito.getId())) {
                throw new RecursoNoEncontradoException(
                        "El ítem no pertenece al carrito del usuario"
                );
        }

        itemCarritoRepository.delete(item);
        itemCarritoRepository.flush();

        return convertirAResponseDTO(carrito);
        }

        @Transactional
        public CarritoResponseDTO actualizarCantidad(
                Long usuarioId,
                Long itemId,
                ActualizarCantidadRequestDTO dto) {

        //Busca el carrito
        Carrito carrito = carritoRepository
                .findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El usuario no tiene un carrito registrado"
                ));

        //Busca el ítem
        ItemCarrito item = itemCarritoRepository
                .findById(itemId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El ítem con ID " + itemId + " no existe"
                ));

        //Verificar que pertenece al carrito
        if (!item.getCarrito().getId().equals(carrito.getId())) {
                throw new RecursoNoEncontradoException(
                        "El ítem no pertenece al carrito del usuario"
                );
        }

        //Consultar el producto en ms-productos
        ProductoDTO producto =
                productoClient.obtenerProducto(item.getProductoId());

        //Validar stock
        if (producto.getStock() == null ||
                dto.getCantidad() > producto.getStock()) {

                throw new IllegalArgumentException(
                        "Stock insuficiente para el producto "
                        + item.getProductoId()
                );
        }

        //Actualizar cantidad y precio
        item.setCantidad(dto.getCantidad());
        item.setPrecioUnitario(producto.getPrecio());

        itemCarritoRepository.saveAndFlush(item);

        //Devolver carrito actualizado
        return convertirAResponseDTO(carrito);
        }

        @Transactional
        public CarritoResponseDTO vaciarCarrito(Long usuarioId) {

        Carrito carrito = carritoRepository
                .findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El usuario no tiene un carrito registrado"
                ));

        //Eliminar todos los ítems del carrito
        List<ItemCarrito> items =
                itemCarritoRepository.findByCarritoId(carrito.getId());

        itemCarritoRepository.deleteAll(items);
        itemCarritoRepository.flush();

        return convertirAResponseDTO(carrito);
        }
}
