package ms_catalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HoraClienteDto {
    private String nombreNodo;
    private long horaEnviada;
    
    
}

