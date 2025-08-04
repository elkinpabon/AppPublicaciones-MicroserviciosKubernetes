package ec.edu.espe.authservice.controller;

import ec.edu.espe.authservice.payload.*;
import ec.edu.espe.authservice.util.JwtUtils;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authManager, JwtUtils jwtUtils) {
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@Valid @RequestBody AuthenticationRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
        String token = jwtUtils.generateJwtToken(auth);
        return new AuthenticationResponse(token);
    }

    @PostMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestBody TokenValidationRequest request) {
        try {
            if (jwtUtils.validateJwtToken(request.getToken())) {
                String username = jwtUtils.getUserNameFromJwtToken(request.getToken());
                String roles = jwtUtils.getRolesFromJwtToken(request.getToken());
                
                return ResponseEntity.ok(new TokenValidationResponse(
                    true, username, roles, "Token válido"
                ));
            } else {
                return ResponseEntity.ok(new TokenValidationResponse(
                    false, null, null, "Token inválido"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new TokenValidationResponse(
                false, null, null, "Error validando token: " + e.getMessage()
            ));
        }
    }
}