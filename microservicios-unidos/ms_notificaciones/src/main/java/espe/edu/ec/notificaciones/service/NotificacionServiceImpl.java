package espe.edu.ec.notificaciones.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import espe.edu.ec.notificaciones.dto.NotificacionDto;
import espe.edu.ec.notificaciones.entity.Notificacion;
import espe.edu.ec.notificaciones.repository.NotificacionRepository;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private NotificacionRepository notificacionRepository;

    @Override
    public List<Notificacion> obtenerTodasLasNotificaciones() {
        try {
            List<Notificacion> notificaciones = notificacionRepository.findAll();
            System.out.println("Servicio obteniendo " + notificaciones.size() + " notificaciones");
            return notificaciones;
        } catch (Exception e) {
            System.err.println("Error en servicio obteniendo notificaciones: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Notificacion crearNotificacion(Notificacion notificacion) {
        try {
            if (notificacion.getFecha() == null) {
                notificacion.setFecha(LocalDateTime.now());
            }
            
            Notificacion nuevaNotificacion = notificacionRepository.save(notificacion);
            
            System.out.println("Notificacion creada exitosamente ID: " + nuevaNotificacion.getId() + " Mensaje: " + nuevaNotificacion.getMensaje());
            
            return nuevaNotificacion;
        } catch (Exception e) {
            System.err.println("Error en servicio creando notificacion: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Notificacion obtenerNotificacionPorId(Long id) {
        try {
            Optional<Notificacion> notificacionOpt = notificacionRepository.findById(id);
            
            if (notificacionOpt.isPresent()) {
                Notificacion notificacion = notificacionOpt.get();
                System.out.println("Notificacion encontrada con ID: " + id);
                return notificacion;
            } else {
                System.out.println("No se encontro notificacion con ID: " + id);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error en servicio obteniendo notificacion por ID: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public long contarNotificaciones() {
        try {
            long total = notificacionRepository.count();
            System.out.println("Total de notificaciones: " + total);
            return total;
        } catch (Exception e) {
            System.err.println("Error en servicio contando notificaciones: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void eliminarNotificacion(Long id) {
        try {
            if (notificacionRepository.existsById(id)) {
                notificacionRepository.deleteById(id);
                System.out.println("Notificacion eliminada con ID: " + id);
            } else {
                System.out.println("No se puede eliminar notificacion no existe con ID: " + id);
                throw new RuntimeException("Notificacion no encontrada con ID: " + id);
            }
        } catch (Exception e) {
            System.err.println("Error en servicio eliminando notificacion: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Notificacion actualizarNotificacion(Notificacion notificacion) {
        try {
            if (notificacionRepository.existsById(notificacion.getId())) {
                if (notificacion.getFecha() == null) {
                    Optional<Notificacion> existente = notificacionRepository.findById(notificacion.getId());
                    if (existente.isPresent()) {
                        notificacion.setFecha(existente.get().getFecha());
                    } else {
                        notificacion.setFecha(LocalDateTime.now());
                    }
                }
                
                Notificacion actualizada = notificacionRepository.save(notificacion);
                System.out.println("Notificacion actualizada con ID: " + notificacion.getId());
                return actualizada;
            } else {
                System.out.println("No se puede actualizar notificacion no existe con ID: " + notificacion.getId());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error en servicio actualizando notificacion: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void enviar(NotificacionDto dto) {
        try {
            rabbitTemplate.convertAndSend("notificaciones.queue", dto);
            System.out.println("Notificacion enviada a queue: " + dto.getMensaje());
        } catch (Exception e) {
            System.err.println("Error enviando notificacion: " + e.getMessage());
        }
    }

    @Override
    public List<NotificacionDto> obtenerTodas() {
        return notificacionRepository.findAll().stream()
                .map(this::convertirADto)
                .toList();
    }

    @Override
    public List<NotificacionDto> obtenerPorTipo(String tipo) {
        return notificacionRepository.findByTipo(tipo).stream()
                .map(this::convertirADto)
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        if (!notificacionRepository.existsById(id)) {
            throw new RuntimeException("Notificacion no encontrada");
        }
        notificacionRepository.deleteById(id);
    }

    @Override
    public long contarTotal() {
        return notificacionRepository.count();
    }

    private NotificacionDto convertirADto(Notificacion notificacion) {
        NotificacionDto dto = new NotificacionDto();
        dto.setId(notificacion.getId());
        dto.setMensaje(notificacion.getMensaje());
        dto.setTipo(notificacion.getTipo());
        dto.setFecha(notificacion.getFecha());
        return dto;
    }
}