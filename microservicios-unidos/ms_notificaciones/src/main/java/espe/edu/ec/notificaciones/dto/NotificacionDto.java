package espe.edu.ec.notificaciones.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDto {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("mensaje")
    private String mensaje;
    
    @JsonProperty("tipo")
    private String tipo;
    
    @JsonProperty("correo")
    private String correo; // Mantener compatibilidad
    
    @JsonProperty("fecha")
    private LocalDateTime fecha;
    
    // Constructor para compatibilidad con el producer de publicaciones
    public NotificacionDto(String mensaje, String tipo) {
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.fecha = LocalDateTime.now();
    }
    
    // Getters y setters
    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}