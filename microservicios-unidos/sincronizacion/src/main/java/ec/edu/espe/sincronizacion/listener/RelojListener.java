package ec.edu.espe.sincronizacion.listener;

import org.springframework.stereotype.Component;

@Component
public class RelojListener {
    
    public RelojListener() {
        System.out.println("RelojListener iniciado sin listeners activos - usar SincronizacionListener");
    }
}