package publicaciones.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import publicaciones.dto.LibroDto;
import publicaciones.model.Autor;
import publicaciones.model.Libro;
import publicaciones.repository.AutorRepository;
import publicaciones.repository.LibroRepository;

@Service
public class LibroService {
    
    @Autowired
    private LibroRepository libroRepository;
    
    @Autowired
    private AutorRepository autorRepository;
    
    public List<LibroDto> getAllLibros() {
        return libroRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public LibroDto getLibroById(Long id) {
        return libroRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));
    }
    
    public List<LibroDto> getLibrosByAutor(Long autorId) {
        return libroRepository.findByAutorId(autorId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<LibroDto> getLibrosByGenero(String genero) {
        return libroRepository.findByGenero(genero).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public LibroDto createLibro(LibroDto libroDto) {
        if (libroDto.getIsbn() != null && libroRepository.existsByIsbn(libroDto.getIsbn())) {
            throw new RuntimeException("Ya existe un libro con este ISBN");
        }
        
        Autor autor = autorRepository.findById(libroDto.getAutorId())
                .orElseThrow(() -> new RuntimeException("Autor no encontrado"));
        
        Libro libro = convertToEntity(libroDto);
        libro.setAutor(autor);
        Libro savedLibro = libroRepository.save(libro);
        return convertToDto(savedLibro);
    }
    
    public LibroDto updateLibro(Long id, LibroDto libroDto) {
        return libroRepository.findById(id)
                .map(libro -> {
                    Autor autor = autorRepository.findById(libroDto.getAutorId())
                            .orElseThrow(() -> new RuntimeException("Autor no encontrado"));
                    
                    libro.setTitulo(libroDto.getTitulo());
                    libro.setAnioPublicacion(libroDto.getAnioPublicacion());
                    libro.setEditorial(libroDto.getEditorial());
                    libro.setIsbn(libroDto.getIsbn());
                    libro.setResumen(libroDto.getResumen());
                    libro.setIdioma(libroDto.getIdioma());
                    libro.setGenero(libroDto.getGenero());
                    libro.setNumeroPaginas(libroDto.getNumeroPaginas());
                    libro.setEdicion(libroDto.getEdicion());
                    libro.setAutor(autor);
                    return convertToDto(libroRepository.save(libro));
                })
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));
    }
    
    public void deleteLibro(Long id) {
        if (!libroRepository.existsById(id)) {
            throw new RuntimeException("Libro no encontrado");
        }
        libroRepository.deleteById(id);
    }
    
    private LibroDto convertToDto(Libro libro) {
        return new LibroDto(
                libro.getId(),
                libro.getTitulo(),
                libro.getAnioPublicacion(),
                libro.getEditorial(),
                libro.getIsbn(),
                libro.getResumen(),
                libro.getIdioma(),
                libro.getGenero(),
                libro.getNumeroPaginas(),
                libro.getEdicion(),
                libro.getAutor().getId(),
                libro.getAutor().getNombre() + " " + libro.getAutor().getApellido()
        );
    }
    
    private Libro convertToEntity(LibroDto libroDto) {
        Libro libro = new Libro();
        libro.setTitulo(libroDto.getTitulo());
        libro.setAnioPublicacion(libroDto.getAnioPublicacion());
        libro.setEditorial(libroDto.getEditorial());
        libro.setIsbn(libroDto.getIsbn());
        libro.setResumen(libroDto.getResumen());
        libro.setIdioma(libroDto.getIdioma());
        libro.setGenero(libroDto.getGenero());
        libro.setNumeroPaginas(libroDto.getNumeroPaginas());
        libro.setEdicion(libroDto.getEdicion());
        return libro;
    }
}