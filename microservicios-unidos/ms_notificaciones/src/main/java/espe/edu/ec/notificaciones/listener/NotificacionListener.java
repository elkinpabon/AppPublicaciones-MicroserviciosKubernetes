package espe.edu.ec.notificaciones.listener;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import espe.edu.ec.notificaciones.entity.Notificacion;
import espe.edu.ec.notificaciones.repository.NotificacionRepository;

@Component
public class NotificacionListener {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "notificaciones.queue")
    public void recibirNotificacion(String mensajeJson) {
        try {
            System.out.println("Mensaje recibido en notificaciones.queue: " + mensajeJson);
            
            Map<String, Object> datos = objectMapper.readValue(mensajeJson, Map.class);
            
            if (datos.containsKey("horaServidor") && datos.containsKey("diferencias")) {
                procesarAjusteSincronizacion(datos);
            } else {
                procesarNotificacionNormal(datos);
            }
            
        } catch (Exception e) {
            System.err.println("Error procesando mensaje: " + e.getMessage());
        }
    }

    private void procesarNotificacionNormal(Map<String, Object> datos) {
        try {
            Notificacion notificacion = new Notificacion();
            notificacion.setMensaje((String) datos.get("mensaje"));
            notificacion.setTipo((String) datos.get("tipo"));
            notificacion.setFecha(LocalDateTime.now());
            
            Notificacion guardada = notificacionRepository.save(notificacion);
            
            System.out.println("Notificacion guardada ID: " + guardada.getId() + " Mensaje: " + guardada.getMensaje());
            
        } catch (Exception e) {
            System.err.println("Error procesando notificacion: " + e.getMessage());
        }
    }

    private void procesarAjusteSincronizacion(Map<String, Object> datos) {
        try {
            long horaServidor = ((Number) datos.get("horaServidor")).longValue();
            Map<String, Object> diferencias = (Map<String, Object>) datos.get("diferencias");
            
            Long miAjuste = null;
            if (diferencias != null && diferencias.containsKey("ms-notificaciones")) {
                miAjuste = ((Number) diferencias.get("ms-notificaciones")).longValue();
            }
            
            System.out.println("Sincronizacion recibida de servidor hora " + horaServidor + " ajuste " + (miAjuste != null ? miAjuste + "ms" : "no requerido"));
            
        } catch (Exception e) {
            System.err.println("Error procesando ajuste de sincronizacion: " + e.getMessage());
        }
    }
}