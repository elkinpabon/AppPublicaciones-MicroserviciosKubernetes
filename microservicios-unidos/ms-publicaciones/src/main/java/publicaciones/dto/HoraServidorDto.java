package publicaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HoraServidorDto {
    private String nombreNodo;
    private long horaRecibida;
    private long horaRespuesta;
}
