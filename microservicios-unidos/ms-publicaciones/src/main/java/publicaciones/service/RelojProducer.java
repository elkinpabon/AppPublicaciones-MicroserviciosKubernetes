package publicaciones.service;

import java.time.Instant;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import publicaciones.dto.HoraClienteDto;

@Service
public class RelojProducer {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private ObjectMapper mapper;

    private static final String NOMBRE_NODO = "ms-publicaciones";

    public void enviarHora() {
        try {
            HoraClienteDto dto = new HoraClienteDto(NOMBRE_NODO, Instant.now().toEpochMilli());
            String json = mapper.writeValueAsString(dto);
            template.convertAndSend("reloj.solicitud", json);
            System.out.println("Publicaciones enviando hora");
        } catch (Exception e) {
            System.err.println("Error enviando hora: " + e.getMessage());
        }
    }
}