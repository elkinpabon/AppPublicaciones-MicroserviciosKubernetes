package publicaciones.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import publicaciones.dto.LibroDto;
import publicaciones.dto.ResponseDto;
import publicaciones.service.LibroService;
import publicaciones.service.NotificacionProducer;

@RestController
@RequestMapping("/api/libros")
@CrossOrigin(origins = "*")
public class LibroController {

    @Autowired
    private LibroService libroService;
    
    @Autowired
    private NotificacionProducer producer;

    @PostMapping
    public ResponseEntity<ResponseDto> createLibro(@RequestBody LibroDto libroDto) {
        try {
            LibroDto createdLibro = libroService.createLibro(libroDto);

            // Enviar notificación limpia
            producer.enviarNotificacion(
                "Nuevo libro creado: " + createdLibro.getTitulo(), 
                "LIBRO"
            );

            // Enviar al catálogo
            producer.enviarCatalogo(createdLibro, "LIBRO");

            System.out.println("Libro creado exitosamente: " + createdLibro.getTitulo());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseDto("Libro creado exitosamente", "ID: " + createdLibro.getId()));
        } catch (Exception e) {
            System.err.println("Error al crear libro: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ResponseDto("Error al crear libro: " + e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<List<LibroDto>> getAllLibros() {
        try {
            List<LibroDto> libros = libroService.getAllLibros();
            System.out.println("Obteniendo " + libros.size() + " libros");
            return ResponseEntity.ok(libros);
        } catch (Exception e) {
            System.err.println("Error obteniendo libros: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibroDto> getLibroById(@PathVariable Long id) {
        try {
            LibroDto libro = libroService.getLibroById(id);
            System.out.println("Libro obtenido por ID: " + id);
            return ResponseEntity.ok(libro);
        } catch (Exception e) {
            System.err.println("Error obteniendo libro por ID " + id + ": " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/autor/{autorId}")
    public ResponseEntity<List<LibroDto>> getLibrosByAutor(@PathVariable Long autorId) {
        try {
            List<LibroDto> libros = libroService.getLibrosByAutor(autorId);
            System.out.println("Libros obtenidos por autor " + autorId + ": " + libros.size());
            return ResponseEntity.ok(libros);
        } catch (Exception e) {
            System.err.println("Error obteniendo libros por autor " + autorId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/genero/{genero}")
    public ResponseEntity<List<LibroDto>> getLibrosByGenero(@PathVariable String genero) {
        try {
            List<LibroDto> libros = libroService.getLibrosByGenero(genero);
            System.out.println("Libros obtenidos por genero " + genero + ": " + libros.size());
            return ResponseEntity.ok(libros);
        } catch (Exception e) {
            System.err.println("Error obteniendo libros por genero " + genero + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updateLibro(@PathVariable Long id, @RequestBody LibroDto libroDto) {
        try {
            LibroDto updatedLibro = libroService.updateLibro(id, libroDto);
            System.out.println("Libro actualizado: " + updatedLibro.getTitulo());
            return ResponseEntity.ok(new ResponseDto("Libro actualizado exitosamente", "ID: " + updatedLibro.getId()));
        } catch (Exception e) {
            System.err.println("Error al actualizar libro " + id + ": " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ResponseDto("Error al actualizar libro: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto> deleteLibro(@PathVariable Long id) {
        try {
            libroService.deleteLibro(id);
            System.out.println("Libro eliminado ID: " + id);
            return ResponseEntity.ok(new ResponseDto("Libro eliminado exitosamente", "ID: " + id));
        } catch (Exception e) {
            System.err.println("Error al eliminar libro " + id + ": " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ResponseDto("Error al eliminar libro: " + e.getMessage(), null));
        }
    }
}