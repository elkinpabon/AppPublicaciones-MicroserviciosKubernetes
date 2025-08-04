package ms_catalogo.repository;

import ms_catalogo.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibroRepository extends JpaRepository<Libro,String> {
}

