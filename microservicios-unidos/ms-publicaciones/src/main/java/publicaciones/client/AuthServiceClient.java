package publicaciones.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import publicaciones.dto.TokenValidationRequest;
import publicaciones.dto.TokenValidationResponse;

@FeignClient(name = "AUTH-SERVICE")
public interface AuthServiceClient {
    
    @PostMapping("/api/auth/validate")
    TokenValidationResponse validateToken(@RequestBody TokenValidationRequest request);
}