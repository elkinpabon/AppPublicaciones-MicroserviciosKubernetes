package publicaciones.service;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RelojListener {
    
    @Autowired
    private ObjectMapper mapper;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @RabbitListener(queues = "sincronizacion.queue")
    public void recibirSincronizacion(String mensaje) {
        try {
            Map<String, Object> syncData = mapper.readValue(mensaje, Map.class);
            String nodoOrigen = (String) syncData.get("nombreNodo");
            Long horaEnviada = ((Number) syncData.get("horaEnviada")).longValue();
            
            System.out.println("Sincronizacion recibida de " + nodoOrigen + " hora " + horaEnviada);
            
        } catch (Exception e) {
            System.err.println("Error procesando sincronizacion: " + e.getMessage());
        }
    }
}