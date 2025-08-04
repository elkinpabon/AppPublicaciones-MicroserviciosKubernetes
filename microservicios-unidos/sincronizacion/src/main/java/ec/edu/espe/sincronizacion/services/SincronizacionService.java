package ec.edu.espe.sincronizacion.services;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.espe.sincronizacion.dto.HoraClienteDto;
import ec.edu.espe.sincronizacion.dto.HoraServidorDto;

@Service
public class SincronizacionService {
    
    private final Map<String, Long> tiemposClientes = new ConcurrentHashMap<>();
    private static final int INTERVALO_SEGUNDOS = 10;
    
    @Autowired
    private RelojProducer relojProducer;

    public void registrarTiempo(HoraClienteDto dto) {
        try {
            tiemposClientes.put(dto.getNombreNodo(), dto.getHoraEnviada());
            
            System.out.println("Tiempo registrado nodo " + dto.getNombreNodo() + " hora " + dto.getHoraEnviada());
            
        } catch (Exception e) {
            System.err.println("Error registrando tiempo: " + e.getMessage());
        }
    }

    public void sincronizarRelojes() {
        try {
            if (tiemposClientes.size() >= 1) {
                long ahora = Instant.now().toEpochMilli();
                
                // Calcular promedio incluyendo el tiempo del servidor
                long sumaTiempos = tiemposClientes.values().stream()
                    .mapToLong(Long::longValue).sum();
                long promedio = (ahora + sumaTiempos) / (tiemposClientes.size() + 1);

                // Calcular diferencias (ajustes) para cada microservicio
                Map<String, Long> diferencias = new HashMap<>();
                
                System.out.println("Ejecutando sincronizacion de relojes");
                System.out.println("Microservicios conectados: " + tiemposClientes.size());
                System.out.println("Hora del servidor: " + ahora + " (" + new Date(ahora) + ")");
                System.out.println("Hora promedio calculada: " + promedio + " (" + new Date(promedio) + ")");
                
                tiemposClientes.forEach((nodo, tiempo) -> {
                    long diferencia = promedio - tiempo;
                    diferencias.put(nodo, diferencia);
                    
                    String ajuste = diferencia > 0 ? "+" + diferencia + "ms" : diferencia + "ms";
                    System.out.println("Ajuste para " + nodo + ": " + ajuste + " tiempo " + tiempo);
                });
                
                // Enviar ajustes a todos los microservicios
                enviarAjusteRelojes(promedio, diferencias);
                
                // Limpiar tiempos registrados para el siguiente ciclo
                tiemposClientes.clear();
                
                System.out.println("Sincronizacion completada y ajustes enviados");
                
            } else {
                System.out.println("Esperando mas microservicios para sincronizar actual: " + tiemposClientes.size());
            }
        } catch (Exception e) {
            System.err.println("Error en sincronizacion de relojes: " + e.getMessage());
        }
    }

    public void enviarAjusteRelojes(long horaServidor, Map<String, Long> diferencias) {
        try {
            System.out.println("Enviando ajustes de relojes a todos los microservicios");
            System.out.println("Hora de sincronizacion: " + horaServidor);
            
            // Crear DTO con la hora del servidor y las diferencias
            HoraServidorDto ajusteDto = new HoraServidorDto(horaServidor, diferencias);
            
            // Enviar a las 3 colas principales
            relojProducer.enviarAjusteAPublicaciones(ajusteDto);
            relojProducer.enviarAjusteANotificaciones(ajusteDto);
            relojProducer.enviarAjusteACatalogo(ajusteDto);
            
            System.out.println("Ajustes de relojes enviados hora " + horaServidor);
            
        } catch (Exception e) {
            System.err.println("Error enviando ajuste de relojes: " + e.getMessage());
        }
    }
}