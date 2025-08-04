package espe.edu.ec.notificaciones.service;

import java.time.Instant;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import espe.edu.ec.notificaciones.dto.HoraClienteDto;

@Service
public class RelojProducer {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private ObjectMapper mapper;

    private static final String NOMBRE_NODO = "ms-notificaciones";

    public void enviarHora() {
        try {
            HoraClienteDto dto = new HoraClienteDto(NOMBRE_NODO, Instant.now().toEpochMilli());
            String json = mapper.writeValueAsString(dto);
            template.convertAndSend("sincronizacion.queue", json);
            System.out.println("Notificaciones enviando hora");
        } catch (Exception e) {
            System.err.println("Error enviando hora: " + e.getMessage());
        }
    }

    public void enviarSincronizacion(Object datos) {
        try {
            String json = mapper.writeValueAsString(datos);
            template.convertAndSend("sincronizacion.queue", json);
            System.out.println("Datos personalizados enviados a sincronizacion");
        } catch (Exception e) {
            System.err.println("Error enviando datos personalizados: " + e.getMessage());
        }
    }
}