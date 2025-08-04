package publicaciones.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "autores")
@Getter
@Setter
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, name = "nombres")
    private String nombre;

    @Column(nullable = false, length = 50, name = "apellidos")
    private String apellido;

    @Column(nullable = false, unique = true, length = 20)
    private String email;

    @Column(nullable = false, unique = true, length = 20)
    private String orcid;

    @Column(length = 50)
    private String nacionalidad;

    @Column(nullable = false, length = 20)
    private String telefono;

    private String institucion;

    @OneToMany(mappedBy = "autor")
    private List<Libro> libros = new ArrayList<>();
}
