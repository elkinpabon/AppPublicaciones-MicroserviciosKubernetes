package ms_catalogo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import ms_catalogo.dto.ArticuloCientificoDto;
import ms_catalogo.dto.LibroDto;
import ms_catalogo.model.ArticuloCientifico;
import ms_catalogo.model.Libro;
import ms_catalogo.repository.ArticuloCientificoRepository;
import ms_catalogo.repository.LibroRepository;

@Service
public class CatalogoServiceImpl implements CatalogoService {

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private ArticuloCientificoRepository articuloRepository;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public void registrarLibro(LibroDto dto) {
        try {
            Libro entidad = mapper.convertValue(dto, Libro.class);
            libroRepository.save(entidad);
            System.out.println("Libro registrado en BD: " + dto.getTitulo());
        } catch (Exception e) {
            System.err.println("Error registrando libro: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void registrarArticulo(ArticuloCientificoDto dto) {
        try {
            ArticuloCientifico entidad = mapper.convertValue(dto, ArticuloCientifico.class);
            articuloRepository.save(entidad);
            System.out.println("Articulo registrado en BD: " + dto.getTitulo());
        } catch (Exception e) {
            System.err.println("Error registrando articulo: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Object> listarTodos() {
        try {
            List<Object> todos = new ArrayList<>();
            todos.addAll(libroRepository.findAll());
            todos.addAll(articuloRepository.findAll());
            System.out.println("Listando todos los elementos: " + todos.size());
            return todos;
        } catch (Exception e) {
            System.err.println("Error listando todos los elementos: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Object> obtenerPorId(String codigo) {
        try {
            return libroRepository.findById(codigo).map(l -> (Object) l)
                    .or(() -> articuloRepository.findById(codigo).map(a -> (Object) a));
        } catch (Exception e) {
            System.err.println("Error obteniendo por ID " + codigo + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<LibroDto> listarLibros() {
        try {
            List<LibroDto> libros = libroRepository.findAll().stream()
                    .map(l -> mapper.convertValue(l, LibroDto.class))
                    .collect(Collectors.toList());
            System.out.println("Listando libros: " + libros.size());
            return libros;
        } catch (Exception e) {
            System.err.println("Error listando libros: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<ArticuloCientificoDto> listarArticulos() {
        try {
            List<ArticuloCientificoDto> articulos = articuloRepository.findAll().stream()
                    .map(a -> mapper.convertValue(a, ArticuloCientificoDto.class))
                    .collect(Collectors.toList());
            System.out.println("Listando articulos: " + articulos.size());
            return articulos;
        } catch (Exception e) {
            System.err.println("Error listando articulos: " + e.getMessage());
            throw e;
        }
    }
}