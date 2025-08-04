package espe.edu.ec.notificaciones.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import espe.edu.ec.notificaciones.entity.Notificacion;
import espe.edu.ec.notificaciones.service.NotificacionService;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @GetMapping
    public ResponseEntity<List<Notificacion>> obtenerTodasLasNotificaciones() {
        try {
            List<Notificacion> notificaciones = notificacionService.obtenerTodasLasNotificaciones();
            
            System.out.println("GET /api/notificaciones - Total encontradas: " + notificaciones.size());
            
            return ResponseEntity.ok(notificaciones);
        } catch (Exception e) {
            System.err.println("Error en obtenerTodasLasNotificaciones: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Notificacion> crearNotificacion(@RequestBody Notificacion notificacion) {
        try {
            System.out.println("POST /api/notificaciones - Creando: " + notificacion.getMensaje());
            
            Notificacion nuevaNotificacion = notificacionService.crearNotificacion(notificacion);
            
            System.out.println("Notificacion creada con ID: " + nuevaNotificacion.getId());
            return ResponseEntity.ok(nuevaNotificacion);
        } catch (Exception e) {
            System.err.println("Error creando notificacion: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> obtenerNotificacionPorId(@PathVariable Long id) {
        try {
            System.out.println("GET /api/notificaciones/" + id + " - Buscando notificacion");
            
            Notificacion notificacion = notificacionService.obtenerNotificacionPorId(id);
            if (notificacion != null) {
                System.out.println("Notificacion encontrada: " + notificacion.getMensaje());
                return ResponseEntity.ok(notificacion);
            } else {
                System.out.println("Notificacion no encontrada con ID: " + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo notificacion por ID " + id + ": " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/estado")
    public ResponseEntity<String> obtenerEstado() {
        System.out.println("GET /api/notificaciones/estado - Verificando estado");
        return ResponseEntity.ok("Servicio de Notificaciones activo y funcionando correctamente");
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        try {
            System.out.println("GET /api/notificaciones/estadisticas - Obteniendo estadisticas");
            
            long totalNotificaciones = notificacionService.contarNotificaciones();
            
            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("totalNotificaciones", totalNotificaciones);
            estadisticas.put("estado", "ACTIVO");
            estadisticas.put("servicio", "MS-Notificaciones");
            estadisticas.put("puerto", 8099);
            estadisticas.put("timestamp", java.time.LocalDateTime.now());
            
            System.out.println("Total notificaciones: " + totalNotificaciones);
            
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            System.err.println("Error obteniendo estadisticas: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarNotificacion(@PathVariable Long id) {
        try {
            System.out.println("DELETE /api/notificaciones/" + id + " - Eliminando notificacion");
            
            notificacionService.eliminarNotificacion(id);
            
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Notificacion eliminada exitosamente");
            respuesta.put("id", id.toString());
            
            System.out.println("Notificacion eliminada: " + id);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            System.err.println("Error eliminando notificacion: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notificacion> actualizarNotificacion(@PathVariable Long id, @RequestBody Notificacion notificacion) {
        try {
            System.out.println("PUT /api/notificaciones/" + id + " - Actualizando notificacion");
            
            notificacion.setId(id);
            Notificacion notificacionActualizada = notificacionService.actualizarNotificacion(notificacion);
            
            if (notificacionActualizada != null) {
                System.out.println("Notificacion actualizada: " + id);
                return ResponseEntity.ok(notificacionActualizada);
            } else {
                System.out.println("Notificacion no encontrada para actualizar: " + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error actualizando notificacion: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}