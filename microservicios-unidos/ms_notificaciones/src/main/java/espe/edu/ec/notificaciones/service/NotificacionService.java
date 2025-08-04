package espe.edu.ec.notificaciones.service;

import java.util.List;

import espe.edu.ec.notificaciones.dto.NotificacionDto;
import espe.edu.ec.notificaciones.entity.Notificacion;

public interface NotificacionService {
    
    // ✅ MÉTODOS BÁSICOS CRUD
    List<Notificacion> obtenerTodasLasNotificaciones();
    Notificacion crearNotificacion(Notificacion notificacion);
    Notificacion obtenerNotificacionPorId(Long id);
    Notificacion actualizarNotificacion(Notificacion notificacion);
    void eliminarNotificacion(Long id);
    long contarNotificaciones();
    
    // ✅ MÉTODOS PARA DTO
    void enviar(NotificacionDto dto);
    List<NotificacionDto> obtenerTodas();
    List<NotificacionDto> obtenerPorTipo(String tipo);
    void eliminar(Long id);
    long contarTotal();
}