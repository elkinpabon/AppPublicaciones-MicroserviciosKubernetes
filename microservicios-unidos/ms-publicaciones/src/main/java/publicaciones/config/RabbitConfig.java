package publicaciones.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    
    // ✅ SOLO LAS 4 COLAS PRINCIPALES NECESARIAS
    @Bean
    public Queue publicacionesQueue() {
        return new Queue("publicaciones.queue", true);
    }
    
    @Bean
    public Queue notificacionesQueue() {
        return new Queue("notificaciones.queue", true);
    }
    
    @Bean
    public Queue catalogoQueue() {
        return new Queue("catalogo.queue", true);
    }
    
    @Bean
    public Queue sincronizacionQueue() {
        return new Queue("sincronizacion.queue", true);
    }
    
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}