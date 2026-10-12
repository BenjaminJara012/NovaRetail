package cl.novaretail.carrito.clients;

import cl.novaretail.carrito.dtos.ProductoDTO;
import cl.novaretail.carrito.exceptions.RecursoNoEncontradoException;
import cl.novaretail.carrito.exceptions.ServicioNoDisponibleException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

@Component
@RequiredArgsConstructor
public class ProductoClient {

    private final RestClient productosRestClient;

    public ProductoDTO obtenerProducto(Long productoId) {

        try {
            ProductoDTO producto = productosRestClient
                    .get()
                    .uri("/api/productos/{id}", productoId)
                    .retrieve()
                    .body(ProductoDTO.class);

            if (producto == null) {
                throw new IllegalStateException(
                        "ms-productos devolvió una respuesta vacía"
                );
            }

            return producto;

        } catch (HttpClientErrorException.NotFound ex) {

            throw new RecursoNoEncontradoException(
                    "El producto con ID " + productoId + " no existe"
            );

        } catch (ResourceAccessException ex) {

            throw new ServicioNoDisponibleException(
                    "No fue posible conectar con ms-productos", ex
            );
        }
    }
}
