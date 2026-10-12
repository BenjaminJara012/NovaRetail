package cl.novaretail.productos.services;

import cl.novaretail.productos.models.Producto;
import cl.novaretail.productos.dtos.ProductoRequestDTO;
import cl.novaretail.productos.dtos.ProductoResponseDTO;
import cl.novaretail.productos.repositories.ProductoRepository;
import cl.novaretail.productos.exceptions.RecursoNoEncontradoException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private ProductoRequestDTO request;

    @BeforeEach
    void setUp() {

        request = new ProductoRequestDTO();
        request.setNombre("Mouse Logitech G203");
        request.setDescripcion("Mouse gamer");
        request.setPrecio(new BigDecimal("24990"));
        request.setStock(10);
    }
    //Test creación producto correctamente
    @Test
    void crearProducto_deberiaCrearProductoCorrectamente() {

        //Simula que el repositorio guarda el producto
        when(productoRepository.save(any(Producto.class)))
                .thenAnswer(invocacion -> {
                    Producto producto = invocacion.getArgument(0);
                    producto.setId(1L);
                    return producto;
                });

        //Ejecutar el método
        ProductoResponseDTO resultado =
                productoService.crearProducto(request);

        //Verificar los datos del producto creado
        assertEquals(1L, resultado.getId());
        assertEquals("Mouse Logitech G203", resultado.getNombre());
        assertEquals("Mouse gamer", resultado.getDescripcion());
        assertEquals(10, resultado.getStock());

        assertEquals(
                0,
                new BigDecimal("24990").compareTo(resultado.getPrecio())
        );

        //Comprobar que se llamó al repositorio
        verify(productoRepository).save(any(Producto.class));
    }

    //TEst buscar producto por ID
    @Test
    void buscarPorId_deberiaRetornarProductoExistente() {

        //Crea un producto simulado
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Mouse Logitech G203");
        producto.setDescripcion("Mouse gamer");
        producto.setPrecio(new BigDecimal("24990"));
        producto.setStock(10);

        //Simula que el repositorio encuentra el producto
        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        //Ejecutar el método del servicio
        ProductoResponseDTO resultado =
                productoService.buscarPorId(1L);

        //Comprobar los datos obtenidos
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Mouse Logitech G203", resultado.getNombre());
        assertEquals(10, resultado.getStock());

        assertEquals(
                0,
                new BigDecimal("24990").compareTo(resultado.getPrecio())
        );

        //Verificar que se consultó el repositorio
        verify(productoRepository).findById(1L);
    }

    //Test busqueda  de un producto inexistente
    @Test
    void buscarPorId_deberiaRechazarProductoInexistente() {

        //Simula que el producto no existe
        when(productoRepository.findById(999L))
                .thenReturn(Optional.empty());

        //Verifica que el servicio lance una excepción
        RecursoNoEncontradoException excepcion = assertThrows(
                RecursoNoEncontradoException.class,
                () -> productoService.buscarPorId(999L)
        );

        //Comprobar el mensaje de error
        assertEquals(
                "Producto no encontrado con ID: 999",
                excepcion.getMessage()
        );

        //Verificar que se consultó el repositorio
        verify(productoRepository).findById(999L);

        //Comprobar que no se intentó guardar ningún producto
        verify(productoRepository, never()).save(any(Producto.class));
    }

    //Test actualización de producto
    @Test
    void actualizarProducto_deberiaActualizarProductoCorrectamente() {

        //Simula un producto existente
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Mouse Logitech G203");
        producto.setDescripcion("Mouse gamer");
        producto.setPrecio(new BigDecimal("24990"));
        producto.setStock(10);

        //Preparar los nuevos datos
        request.setNombre("Mouse Logitech G305");
        request.setDescripcion("Mouse gamer inalámbrico");
        request.setPrecio(new BigDecimal("39990"));
        request.setStock(15);

        //Simular la búsqueda del producto
        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        //Simular el guardado de los cambios
        when(productoRepository.save(any(Producto.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        
        ProductoResponseDTO resultado =
                productoService.actualizarProducto(1L, request);

        // Comprobar los datos actualizados
        assertEquals(1L, resultado.getId());
        assertEquals("Mouse Logitech G305", resultado.getNombre());
        assertEquals("Mouse gamer inalámbrico", resultado.getDescripcion());
        assertEquals(15, resultado.getStock());

        assertEquals(
                0,
                new BigDecimal("39990").compareTo(resultado.getPrecio())
        );

      
        verify(productoRepository).findById(1L);
        verify(productoRepository).save(producto);
    }

}
