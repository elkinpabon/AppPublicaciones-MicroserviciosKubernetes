package publicaciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsPublicacionesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsPublicacionesApplication.class, args);
    }
}