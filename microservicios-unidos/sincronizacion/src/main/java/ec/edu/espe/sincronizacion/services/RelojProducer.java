package ec.edu.espe.sincronizacion.services;

import java.time.Instant;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import ec.edu.espe.sincronizacion.dto.HoraClienteDto;
import ec.edu.espe.sincronizacion.dto.HoraServidorDto;

@Service
public class RelojProducer {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private ObjectMapper mapper;

    private static final String NOMBRE_NODO = "ms-sincronizacion";

    public void enviarHora() {
        try {
            HoraClienteDto dto = new HoraClienteDto(NOMBRE_NODO, Instant.now().toEpochMilli());
            String json = mapper.writeValueAsString(dto);
            
            template.convertAndSend("sincronizacion.queue", json);
            System.out.println("Sincronizacion enviando hora");
            
        } catch (Exception e) {
            System.err.println("Error enviando hora desde " + NOMBRE_NODO + ": " + e.getMessage());
        }
    }

    public void enviarAjusteAPublicaciones(HoraServidorDto ajuste) {
        try {
            String json = mapper.writeValueAsString(ajuste);
            template.convertAndSend("publicaciones.queue", json);
            System.out.println("Ajuste enviado a publicaciones.queue hora " + ajuste.getHoraServidor());
        } catch (Exception e) {
            System.err.println("Error enviando ajuste a publicaciones: " + e.getMessage());
        }
    }

    public void enviarAjusteANotificaciones(HoraServidorDto ajuste) {
        try {
            String json = mapper.writeValueAsString(ajuste);
            template.convertAndSend("notificaciones.queue", json);
            System.out.println("Ajuste enviado a notificaciones.queue hora " + ajuste.getHoraServidor());
        } catch (Exception e) {
            System.err.println("Error enviando ajuste a notificaciones: " + e.getMessage());
        }
    }

    public void enviarAjusteACatalogo(HoraServidorDto ajuste) {
        try {
            String json = mapper.writeValueAsString(ajuste);
            template.convertAndSend("catalogo.queue", json);
            System.out.println("Ajuste enviado a catalogo.queue hora " + ajuste.getHoraServidor());
        } catch (Exception e) {
            System.err.println("Error enviando ajuste a catalogo: " + e.getMessage());
        }
    }
}