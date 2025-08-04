package publicaciones.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "Articulos")
@Getter
@Setter
public class Articulo extends Publicacion {

    private String revista;
    @Column(name = "doi", unique = true)
    private String doi;
    private String areaInvestigacion;
    private String fechaPublicacion;


    @ManyToOne
    @JoinColumn(name = "id_autor")
    private Autor autor;
}
