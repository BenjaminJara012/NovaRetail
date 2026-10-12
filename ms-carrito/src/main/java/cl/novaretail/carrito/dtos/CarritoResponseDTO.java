package cl.novaretail.carrito.dtos;

import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class CarritoResponseDTO {

    private Long id;
    private Long usuarioId;
    private List<ItemCarritoResponseDTO> items;
    private BigDecimal total;
}
