package cl.novaretail.carrito.repositories;

import cl.novaretail.carrito.models.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {

    Optional<ItemCarrito> findByCarritoIdAndProductoId(
            Long carritoId,
            Long productoId
    );

    List<ItemCarrito> findByCarritoId(Long carritoId);
}
