package publicaciones.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "Libros")
@Getter
@Setter
public class Libro extends Publicacion {

    private String genero;
    private int numeroPaginas;
    private String edicion;

    @ManyToOne
    @JoinColumn(name = "id_autor")
    private Autor autor;
}
