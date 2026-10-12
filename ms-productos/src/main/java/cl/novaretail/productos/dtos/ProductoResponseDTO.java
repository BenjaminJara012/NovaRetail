package cl.novaretail.productos.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProductoResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
}
