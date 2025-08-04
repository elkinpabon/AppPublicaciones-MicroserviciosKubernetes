package publicaciones.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import publicaciones.model.Articulo;

@Repository
public interface ArticuloRepository extends JpaRepository<Articulo, Long> {
    List<Articulo> findByAutorId(Long autorId);
    List<Articulo> findByAreaInvestigacion(String areaInvestigacion);
    List<Articulo> findByRevista(String revista);
    Optional<Articulo> findByDoi(String doi);
    boolean existsByDoi(String doi);
}