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

import publicaciones.dto.ArticuloDto;
import publicaciones.dto.ResponseDto;
import publicaciones.service.ArticuloService;
import publicaciones.service.NotificacionProducer;

@RestController
@RequestMapping("/api/articulos")
@CrossOrigin(origins = "*")
public class ArticuloController {
    
    @Autowired
    private ArticuloService articuloService;
    
    @Autowired
    private NotificacionProducer producer;
    
    @PostMapping
    public ResponseEntity<ResponseDto> createArticulo(@RequestBody ArticuloDto articuloDto) {
        try {
            ArticuloDto createdArticulo = articuloService.createArticulo(articuloDto);
            
            // Enviar notificación limpia
            producer.enviarNotificacion(
                "Nuevo articulo creado: " + createdArticulo.getTitulo(), 
                "ARTICULO"
            );
            
            // Enviar al catálogo
            producer.enviarCatalogo(createdArticulo, "ARTICULO");
            
            System.out.println("Articulo creado exitosamente: " + createdArticulo.getTitulo());
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseDto("Artículo creado exitosamente", "ID: " + createdArticulo.getId()));
        } catch (Exception e) {
            System.err.println("Error al crear articulo: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ResponseDto("Error al crear artículo: " + e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<List<ArticuloDto>> getAllArticulos() {
        try {
            List<ArticuloDto> articulos = articuloService.getAllArticulos();
            System.out.println("Obteniendo " + articulos.size() + " articulos");
            return ResponseEntity.ok(articulos);
        } catch (Exception e) {
            System.err.println("Error obteniendo articulos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticuloDto> getArticuloById(@PathVariable Long id) {
        try {
            ArticuloDto articulo = articuloService.getArticuloById(id);
            System.out.println("Articulo obtenido por ID: " + id);
            return ResponseEntity.ok(articulo);
        } catch (Exception e) {
            System.err.println("Error obteniendo articulo por ID " + id + ": " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/autor/{autorId}")
    public ResponseEntity<List<ArticuloDto>> getArticulosByAutor(@PathVariable Long autorId) {
        try {
            List<ArticuloDto> articulos = articuloService.getArticulosByAutor(autorId);
            System.out.println("Articulos obtenidos por autor " + autorId + ": " + articulos.size());
            return ResponseEntity.ok(articulos);
        } catch (Exception e) {
            System.err.println("Error obteniendo articulos por autor " + autorId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/area/{areaInvestigacion}")
    public ResponseEntity<List<ArticuloDto>> getArticulosByAreaInvestigacion(@PathVariable String areaInvestigacion) {
        try {
            List<ArticuloDto> articulos = articuloService.getArticulosByAreaInvestigacion(areaInvestigacion);
            System.out.println("Articulos obtenidos por area " + areaInvestigacion + ": " + articulos.size());
            return ResponseEntity.ok(articulos);
        } catch (Exception e) {
            System.err.println("Error obteniendo articulos por area " + areaInvestigacion + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/revista/{revista}")
    public ResponseEntity<List<ArticuloDto>> getArticulosByRevista(@PathVariable String revista) {
        try {
            List<ArticuloDto> articulos = articuloService.getArticulosByRevista(revista);
            System.out.println("Articulos obtenidos por revista " + revista + ": " + articulos.size());
            return ResponseEntity.ok(articulos);
        } catch (Exception e) {
            System.err.println("Error obteniendo articulos por revista " + revista + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updateArticulo(@PathVariable Long id, @RequestBody ArticuloDto articuloDto) {
        try {
            ArticuloDto updatedArticulo = articuloService.updateArticulo(id, articuloDto);
            System.out.println("Articulo actualizado: " + updatedArticulo.getTitulo());
            return ResponseEntity.ok(new ResponseDto("Artículo actualizado exitosamente", "ID: " + updatedArticulo.getId()));
        } catch (Exception e) {
            System.err.println("Error al actualizar articulo " + id + ": " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ResponseDto("Error al actualizar artículo: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto> deleteArticulo(@PathVariable Long id) {
        try {
            articuloService.deleteArticulo(id);
            System.out.println("Articulo eliminado ID: " + id);
            return ResponseEntity.ok(new ResponseDto("Artículo eliminado exitosamente", "ID: " + id));
        } catch (Exception e) {
            System.err.println("Error al eliminar articulo " + id + ": " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ResponseDto("Error al eliminar artículo: " + e.getMessage(), null));
        }
    }
}