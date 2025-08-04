package publicaciones.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import publicaciones.service.RelojProducer;

@Configuration
@EnableScheduling
public class SchedullingConfig {
    @Autowired
    private RelojProducer relojProducer;

    @Scheduled(fixedRate = 10000)
    public void reportarhora(){
        try{
            relojProducer.enviarHora();
            System.out.println("Nodo: ms-publicaciones -> Enviado hora");

        }catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
}
