package publicaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticuloDto {
    private Long id;
    private String titulo;
    private int anioPublicacion;
    private String editorial;
    private String isbn;
    private String resumen;
    private String idioma;
    private String revista;
    private String doi;
    private String areaInvestigacion;
    private String fechaPublicacion;
    private Long autorId;
    private String autorNombre;
}