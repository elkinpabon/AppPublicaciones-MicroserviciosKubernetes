package publicaciones.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import publicaciones.model.Autor;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByEmail(String email);
    Optional<Autor> findByOrcid(String orcid);
    boolean existsByEmail(String email);
    boolean existsByOrcid(String orcid);
}