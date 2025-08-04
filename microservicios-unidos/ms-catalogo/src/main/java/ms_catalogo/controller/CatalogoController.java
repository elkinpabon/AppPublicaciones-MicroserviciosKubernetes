package ms_catalogo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import ms_catalogo.dto.ArticuloCientificoDto;
import ms_catalogo.dto.LibroDto;
import ms_catalogo.service.CatalogoService;

@RestController
@RequestMapping("/api/catalogo")
@CrossOrigin(origins = "*")
public class CatalogoController {

    @Autowired
    private CatalogoService service;

    @Autowired
    private ObjectMapper mapper;

    @GetMapping
    public ResponseEntity<List<Object>> listarTodos() {
        try {
            List<Object> todos = service.listarTodos();
            System.out.println("GET /api/catalogo - Total elementos: " + todos.size());
            return ResponseEntity.ok(todos);
        } catch (Exception e) {
            System.err.println("Error obteniendo todos los elementos: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Object> obtenerPorId(@PathVariable String codigo) {
        try {
            System.out.println("GET /api/catalogo/" + codigo + " - Buscando elemento");
            return service.obtenerPorId(codigo)
                    .map(elemento -> {
                        System.out.println("Elemento encontrado: " + codigo);
                        return ResponseEntity.ok(elemento);
                    })
                    .orElseGet(() -> {
                        System.out.println("Elemento no encontrado: " + codigo);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            System.err.println("Error obteniendo elemento por codigo " + codigo + ": " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/libros")
    public ResponseEntity<List<LibroDto>> listarLibros() {
        try {
            List<LibroDto> libros = service.listarLibros();
            System.out.println("GET /api/catalogo/libros - Total libros: " + libros.size());
            return ResponseEntity.ok(libros);
        } catch (Exception e) {
            System.err.println("Error obteniendo libros: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/articulos")
    public ResponseEntity<List<ArticuloCientificoDto>> listarArticulos() {
        try {
            List<ArticuloCientificoDto> articulos = service.listarArticulos();
            System.out.println("GET /api/catalogo/articulos - Total articulos: " + articulos.size());
            return ResponseEntity.ok(articulos);
        } catch (Exception e) {
            System.err.println("Error obteniendo articulos: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        try {
            System.out.println("GET /api/catalogo/estadisticas - Obteniendo estadisticas");
            
            long totalLibros = service.listarLibros().size();
            long totalArticulos = service.listarArticulos().size();
            long total = totalLibros + totalArticulos;
            
            Map<String, Object> estadisticas = Map.of(
                "totalLibros", totalLibros,
                "totalArticulos", totalArticulos,
                "totalElementos", total,
                "estado", "Catalogo funcionando correctamente",
                "timestamp", System.currentTimeMillis()
            );
            
            System.out.println("Estadisticas - Libros: " + totalLibros + " Articulos: " + totalArticulos + " Total: " + total);
            
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            System.err.println("Error obteniendo estadisticas: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/estado")
    public ResponseEntity<String> estadoServicio() {
        System.out.println("GET /api/catalogo/estado - Verificando estado");
        return ResponseEntity.ok("Servicio de Catalogo activo y guardando en PostgreSQL");
    }

    @PostMapping
    public ResponseEntity<String> crearItem(@RequestBody Map<?,?> payload) {
        try {
            if (payload.containsKey("numeroPaginas")) {
                LibroDto libro = mapper.convertValue(payload, LibroDto.class);
                service.registrarLibro(libro);
                System.out.println("Libro creado via POST: " + libro.getTitulo());
                return ResponseEntity.status(201).body("Libro creado y guardado en PostgreSQL");
            } else if (payload.containsKey("areaInvestigacion")) {
                ArticuloCientificoDto art = mapper.convertValue(payload, ArticuloCientificoDto.class);
                service.registrarArticulo(art);
                System.out.println("Articulo creado via POST: " + art.getTitulo());
                return ResponseEntity.status(201).body("Articulo creado y guardado en PostgreSQL");
            } else {
                System.err.println("Payload no reconocido en POST");
                return ResponseEntity.badRequest().body("Payload no reconocido");
            }
        } catch (Exception e) {
            System.err.println("Error creando item: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error creando item: " + e.getMessage());
        }
    }
}