package ec.edu.espe.sincronizacion.listener;

import java.util.Date;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import ec.edu.espe.sincronizacion.dto.HoraClienteDto;
import ec.edu.espe.sincronizacion.services.SincronizacionService;

@Component
public class SincronizacionListener {

    @Autowired
    private SincronizacionService sincronizacionService;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "sincronizacion.queue")
    public void recibirSincronizacion(String mensajeJson) {
        try {
            System.out.println("Mensaje recibido en sincronizacion.queue: " + mensajeJson);
            
            // Parsear el mensaje JSON que viene de cualquier microservicio
            Map<String, Object> syncData = objectMapper.readValue(mensajeJson, Map.class);
            
            // Verificar si es un HoraClienteDto
            if (syncData.containsKey("nombreNodo") && syncData.containsKey("horaEnviada")) {
                // Es un mensaje de sincronización de reloj
                HoraClienteDto dto = objectMapper.convertValue(syncData, HoraClienteDto.class);
                procesarSincronizacionReloj(dto);
                
            } else {
                // Son datos generales de sincronización
                procesarDatosGenerales(syncData);
            }
            
        } catch (Exception e) {
            System.err.println("Error procesando sincronizacion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void procesarSincronizacionReloj(HoraClienteDto dto) {
        try {
            System.out.println("Procesando sincronizacion de reloj desde " + dto.getNombreNodo());
            
            // Registrar el tiempo en el servicio de sincronización
            sincronizacionService.registrarTiempo(dto);
            
            System.out.println("Sincronizacion de reloj procesada nodo " + dto.getNombreNodo() + 
                             " hora " + dto.getHoraEnviada() + " fecha " + new Date(dto.getHoraEnviada()));
            
        } catch (Exception e) {
            System.err.println("Error procesando sincronizacion de reloj: " + e.getMessage());
        }
    }

    private void procesarDatosGenerales(Map<String, Object> datos) {
        try {
            System.out.println("Procesando datos generales de sincronizacion");
            
            datos.forEach((key, value) -> {
                System.out.println("Dato " + key + ": " + value);
            });
            
            System.out.println("Datos de sincronizacion procesados");
            
        } catch (Exception e) {
            System.err.println("Error procesando datos generales: " + e.getMessage());
        }
    }
}