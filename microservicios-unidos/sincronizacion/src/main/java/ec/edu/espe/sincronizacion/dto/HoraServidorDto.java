package ec.edu.espe.sincronizacion.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HoraServidorDto {
    private long horaServidor;
    private Map<String, Long> diferencias;
}