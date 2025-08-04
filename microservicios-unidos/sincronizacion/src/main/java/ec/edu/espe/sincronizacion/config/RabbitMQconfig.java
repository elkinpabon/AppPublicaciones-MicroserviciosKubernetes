package ec.edu.espe.sincronizacion.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQconfig {

    public static final String COLA_SINCRONIZACION = "sincronizacion.queue";
    public static final String COLA_PUBLICACIONES = "publicaciones.queue";
    public static final String COLA_NOTIFICACIONES = "notificaciones.queue";
    public static final String COLA_CATALOGO = "catalogo.queue";

    @Bean
    public Queue sincronizacionQueue() {
        Queue queue = QueueBuilder.durable(COLA_SINCRONIZACION).build();
        System.out.println("Cola configurada: " + COLA_SINCRONIZACION);
        return queue;
    }
    
    @Bean
    public Queue publicacionesQueue() {
        return QueueBuilder.durable(COLA_PUBLICACIONES).build();
    }
    
    @Bean
    public Queue notificacionesQueue() {
        return QueueBuilder.durable(COLA_NOTIFICACIONES).build();
    }
    
    @Bean
    public Queue catalogoQueue() {
        return QueueBuilder.durable(COLA_CATALOGO).build();
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        System.out.println("RabbitTemplate configurado correctamente");
        return template;
    }
}