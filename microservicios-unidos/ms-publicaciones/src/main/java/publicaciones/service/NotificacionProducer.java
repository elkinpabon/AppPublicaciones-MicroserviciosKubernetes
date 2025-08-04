package publicaciones.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import publicaciones.dto.NotificacionDto;

@Service
public class NotificacionProducer {
    
    @Autowired
    private RabbitTemplate template;
    
    @Autowired
    private ObjectMapper mapper;
    
    // Constantes de las colas
    private static final String COLA_NOTIFICACIONES = "notificaciones.queue";
    private static final String COLA_CATALOGO = "catalogo.queue";
    private static final String COLA_SINCRONIZACION = "sincronizacion.queue";
    
    // Enviar notificación
    public void enviarNotificacion(String mensaje, String tipo) {
        try {
            NotificacionDto dto = new NotificacionDto(mensaje, tipo);
            String json = mapper.writeValueAsString(dto);
            template.convertAndSend(COLA_NOTIFICACIONES, json);
            System.out.println("Notificacion enviada: " + mensaje);
        } catch (Exception e) {
            System.err.println("Error enviando notificacion: " + e.getMessage());
        }
    }
    
    // Enviar datos al catálogo
    public void enviarCatalogo(Object objeto, String tipo) {
        try {
            Map<String, Object> datos = new HashMap<>();
            datos.put("tipo", tipo);
            datos.put("datos", objeto);
            datos.put("timestamp", System.currentTimeMillis());
            
            String json = mapper.writeValueAsString(datos);
            template.convertAndSend(COLA_CATALOGO, json);
            System.out.println("Datos enviados al catalogo tipo: " + tipo);
        } catch (Exception e) {
            System.err.println("Error enviando al catalogo: " + e.getMessage());
        }
    }
    
    // Enviar datos de sincronización
    public void enviarSincronizacion(Object datos) {
        try {
            String json = mapper.writeValueAsString(datos);
            template.convertAndSend(COLA_SINCRONIZACION, json);
            System.out.println("Datos de sincronizacion enviados");
        } catch (Exception e) {
            System.err.println("Error enviando sincronizacion: " + e.getMessage());
        }
    }
}