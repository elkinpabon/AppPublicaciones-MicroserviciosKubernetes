package ms_catalogo.service;

import java.time.Instant;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import ms_catalogo.dto.HoraClienteDto;

@Service
public class RelojProducer {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private ObjectMapper mapper;

    private static final String NOMBRE_NODO = "ms-catalogo";

    public void enviarHora() {
        try {
            HoraClienteDto dto = new HoraClienteDto(NOMBRE_NODO, Instant.now().toEpochMilli());
            String json = mapper.writeValueAsString(dto);
            template.convertAndSend("sincronizacion.queue", json);
            System.out.println("Catalogo enviando hora");
        } catch (Exception e) {
            System.err.println("Error enviando hora: " + e.getMessage());
        }
    }
}