package ms_catalogo.service;

import ms_catalogo.dto.LibroDto;
import ms_catalogo.dto.ArticuloCientificoDto;
import java.util.List;
import java.util.Optional;

public interface CatalogoService {
    void registrarLibro(LibroDto dto);
    void registrarArticulo(ArticuloCientificoDto dto);
    List<Object> listarTodos();
    Optional<Object> obtenerPorId(String codigo);
    List<LibroDto> listarLibros();
    List<ArticuloCientificoDto> listarArticulos();
}

