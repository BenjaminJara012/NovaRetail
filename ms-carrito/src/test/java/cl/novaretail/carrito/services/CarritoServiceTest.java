package cl.novaretail.carrito.services;

import cl.novaretail.carrito.clients.ProductoClient;
import cl.novaretail.carrito.dtos.AgregarItemRequestDTO;
import cl.novaretail.carrito.dtos.ProductoDTO;
import cl.novaretail.carrito.dtos.CarritoResponseDTO;
import cl.novaretail.carrito.models.*;
import cl.novaretail.carrito.repositories.CarritoRepository;
import cl.novaretail.carrito.repositories.ItemCarritoRepository;
import cl.novaretail.carrito.services.CarritoService;
import cl.novaretail.carrito.exceptions.RecursoNoEncontradoException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.List;



@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private ItemCarritoRepository itemCarritoRepository;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private CarritoService carritoService;

    private ProductoDTO producto;
    private AgregarItemRequestDTO request;

    @BeforeEach
    void configurarDatos() {

        producto = new ProductoDTO();
        producto.setId(2L);
        producto.setNombre("Mouse Logitech G203");
        producto.setPrecio(new BigDecimal("24990"));
        producto.setStock(5);

        request = new AgregarItemRequestDTO();
        request.setProductoId(2L);
        request.setCantidad(10);
    }


    @Test
    void agregarItem_deberiaRechazarCantidadMayorAlStock() {

        Carrito carrito = new Carrito();
        carrito.setId(1L);
        carrito.setUsuarioId(1L);

        when(productoClient.obtenerProducto(2L))
                .thenReturn(producto);

        when(carritoRepository.findByUsuarioId(1L))
                .thenReturn(Optional.of(carrito));

        when(itemCarritoRepository.findByCarritoIdAndProductoId(
                1L, 2L
        )).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> carritoService.agregarItem(1L, request)
        );

        verify(itemCarritoRepository, never()).save(any());
    }

    //Simular que ms-productos responde que el item/producto no existe
    @Test
    void agregarItem_deberiaRechazarProductoInexistente() {

        when(productoClient.obtenerProducto(2L))
                .thenThrow(new RecursoNoEncontradoException(
                        "El producto con ID 2 no existe"
                ));

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> carritoService.agregarItem(1L, request)
        );

        verify(carritoRepository, never()).save(any());
        verify(itemCarritoRepository, never()).save(any());
    }


    //TEst probar que se agregue un producto correctamente
    @Test
    void agregarItem_deberiaAgregarProductoCorrectamente() {

        //Simula un producto con stock disponible
        producto.setStock(10);
        request.setCantidad(2);

        //Simula un carrito existente
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        carrito.setUsuarioId(1L);

        //Configurar respuestas simuladas
        when(productoClient.obtenerProducto(2L))
                .thenReturn(producto);

        when(carritoRepository.findByUsuarioId(1L))
                .thenReturn(Optional.of(carrito));

        when(itemCarritoRepository.findByCarritoIdAndProductoId(1L, 2L))
                .thenReturn(Optional.empty());

        //Simular que el repositorio asigna un ID al ítem
        when(itemCarritoRepository.save(any(ItemCarrito.class)))
                .thenAnswer(invocacion -> {
                    ItemCarrito item = invocacion.getArgument(0);
                    item.setId(1L);
                    return item;
                });

        //Simular los ítems que devuelve la base de datos
        when(itemCarritoRepository.findByCarritoId(1L))
                .thenAnswer(invocacion -> {

                    ItemCarrito item = new ItemCarrito();
                    item.setId(1L);
                    item.setProductoId(2L);
                    item.setCantidad(2);
                    item.setPrecioUnitario(new BigDecimal("24990"));
                    item.setCarrito(carrito);

                    return List.of(item);
                });

        //Ejecutar el método que queremos probar
        CarritoResponseDTO resultado =
                carritoService.agregarItem(1L, request);

        //Comprobar los resultados
        assertEquals(1L, resultado.getUsuarioId());
        assertEquals(1, resultado.getItems().size());
        assertEquals(2, resultado.getItems().get(0).getCantidad());

        assertEquals(
                0,
                new BigDecimal("49980").compareTo(resultado.getTotal())
        );

        verify(itemCarritoRepository).save(any(ItemCarrito.class));
    }


    //Test probar el aumento de cantidad de un producto existente 
    @Test
    void agregarItem_deberiaAumentarCantidadSiProductoYaExiste() {

        //Preparar producto y solicitud
        producto.setStock(10);
        request.setCantidad(3);

        //Simula carrito existente
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        carrito.setUsuarioId(1L);

        //Simula un ítem que ya tiene 2 unidades
        ItemCarrito itemExistente = new ItemCarrito();
        itemExistente.setId(1L);
        itemExistente.setProductoId(2L);
        itemExistente.setCantidad(2);
        itemExistente.setPrecioUnitario(new BigDecimal("24990"));
        itemExistente.setCarrito(carrito);

        //Configurar los mocks
        when(productoClient.obtenerProducto(2L))
                .thenReturn(producto);

        when(carritoRepository.findByUsuarioId(1L))
                .thenReturn(Optional.of(carrito));

        when(itemCarritoRepository.findByCarritoIdAndProductoId(1L, 2L))
                .thenReturn(Optional.of(itemExistente));

        when(itemCarritoRepository.findByCarritoId(1L))
                .thenReturn(List.of(itemExistente));

        //Ejecutar el método
        CarritoResponseDTO resultado =
                carritoService.agregarItem(1L, request);

        //Comprobar que se actualizó el mismo ítem
        assertEquals(1, resultado.getItems().size());
        assertEquals(5, resultado.getItems().get(0).getCantidad());

        assertEquals(
                0,
                new BigDecimal("124950").compareTo(resultado.getTotal())
        );

        //Veerificar que se guardó el ítem existente
        verify(itemCarritoRepository).save(itemExistente);
        verify(carritoRepository, never()).save(any());
    }

    //Test no se puede eliminar items de otro carrito
    @Test
    void eliminarItem_deberiaRechazarItemDeOtroCarrito() {

        //Carrito perteneciente al usuario 1
        Carrito carritoUsuario1 = new Carrito();
        carritoUsuario1.setId(1L);
        carritoUsuario1.setUsuarioId(1L);

        //Carrito perteneciente al usuario 2
        Carrito carritoUsuario2 = new Carrito();
        carritoUsuario2.setId(2L);
        carritoUsuario2.setUsuarioId(2L);

        //Ítem perteneciente al carrito del usuario 2
        ItemCarrito itemAjeno = new ItemCarrito();
        itemAjeno.setId(5L);
        itemAjeno.setProductoId(2L);
        itemAjeno.setCantidad(2);
        itemAjeno.setCarrito(carritoUsuario2);

        //Simular las consultas a los repositorios
        when(carritoRepository.findByUsuarioId(1L))
                .thenReturn(Optional.of(carritoUsuario1));

        when(itemCarritoRepository.findById(5L))
                .thenReturn(Optional.of(itemAjeno));

        //Intentar eliminar el ítem ajeno
        assertThrows(
                RecursoNoEncontradoException.class,
                () -> carritoService.eliminarItem(1L, 5L)
        );

        //Verificar que no se eliminó ningún ítem
        verify(itemCarritoRepository, never())
                .delete(any(ItemCarrito.class));
    }

}
