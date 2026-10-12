package cl.novaretail.productos.services;

import cl.novaretail.productos.models.Producto;
import cl.novaretail.productos.dtos.ProductoRequestDTO;
import cl.novaretail.productos.dtos.ProductoResponseDTO;
import cl.novaretail.productos.repositories.ProductoRepository;
import cl.novaretail.productos.exceptions.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    //Listar todos los productos
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarProductos() {
        return productoRepository.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    //Buscar producto por ID
    @Transactional(readOnly = true)
    public ProductoResponseDTO buscarPorId(Long id) {
        Producto producto = obtenerProducto(id);
        return convertirAResponseDTO(producto);
    }

    //Crear producto
    @Transactional
    public ProductoResponseDTO crearProducto(ProductoRequestDTO dto) {

        Producto producto = new Producto();

        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());

        Producto guardado = productoRepository.save(producto);

        return convertirAResponseDTO(guardado);
    }

    //Actualizar producto
    @Transactional
    public ProductoResponseDTO actualizarProducto(
            Long id,
            ProductoRequestDTO dto) {

        Producto producto = obtenerProducto(id);

        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());

        Producto actualizado = productoRepository.save(producto);

        return convertirAResponseDTO(actualizado);
    }

    //Eliminar producto
    @Transactional
    public void eliminarProducto(Long id) {

        Producto producto = obtenerProducto(id);

        productoRepository.delete(producto);
    }

    private Producto obtenerProducto(Long id) {
    return productoRepository.findById(id)
            .orElseThrow(() ->
                    new RecursoNoEncontradoException(
                            "Producto no encontrado con ID: " + id
                    )
            );
}

    //Convertir entidad a DTO respuesta
    private ProductoResponseDTO convertirAResponseDTO(Producto producto) {

        ProductoResponseDTO dto = new ProductoResponseDTO();

        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());

        return dto;
    }
}
