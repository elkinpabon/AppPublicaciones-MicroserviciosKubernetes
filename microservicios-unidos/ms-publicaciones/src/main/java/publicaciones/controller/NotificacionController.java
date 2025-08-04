package publicaciones.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import publicaciones.service.NotificacionProducer;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionProducer producer;

    @GetMapping
    public String enviar(@RequestParam String mensaje, @RequestParam String tipo) {
        producer.enviarNotificacion(mensaje, tipo);
        return "✅ Enviado desde ms-publicaciones a cola.publicaciones";
    }
}
