package ms_catalogo.listener;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SincronizacionListener {

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "sincronizacion.queue")
    public void recibirSincronizacion(String mensajeJson) {
        try {
            Map<String, Object> syncData = objectMapper.readValue(mensajeJson, Map.class);
            String nodoOrigen = (String) syncData.get("nombreNodo");
            Long horaEnviada = null;
            
            if (syncData.get("horaEnviada") != null) {
                horaEnviada = ((Number) syncData.get("horaEnviada")).longValue();
            }
            
            System.out.println("Sincronizacion recibida de " + nodoOrigen + " hora " + (horaEnviada != null ? horaEnviada : "null"));
            
        } catch (Exception e) {
            System.err.println("Error procesando sincronizacion: " + e.getMessage());
        }
    }
}