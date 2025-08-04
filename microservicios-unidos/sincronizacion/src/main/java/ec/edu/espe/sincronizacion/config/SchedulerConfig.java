package ec.edu.espe.sincronizacion.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import ec.edu.espe.sincronizacion.services.RelojProducer;
import ec.edu.espe.sincronizacion.services.SincronizacionService;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    private SincronizacionService sincronizacionService;
    
    @Autowired
    private RelojProducer relojProducer;

    @Scheduled(fixedRateString = "#{T(java.util.concurrent.TimeUnit).SECONDS.toMillis(${reloj.intervalo:10})}")
    public void sincronizarRelojes() {
        try {
            System.out.println("Ejecutando sincronizacion automatica");
            sincronizacionService.sincronizarRelojes();
        } catch (Exception e) {
            System.err.println("Error en sincronizacion automatica: " + e.getMessage());
        }
    }

    @Scheduled(fixedRateString = "#{T(java.util.concurrent.TimeUnit).SECONDS.toMillis(${reloj.intervalo:10})}")
    public void autoRegistro() {
        try {
            relojProducer.enviarHora();
        } catch (Exception e) {
            System.err.println("Error en auto-registro: " + e.getMessage());
        }
    }
}