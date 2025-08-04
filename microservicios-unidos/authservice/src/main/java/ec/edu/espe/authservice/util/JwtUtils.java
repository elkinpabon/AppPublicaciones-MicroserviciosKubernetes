package ec.edu.espe.authservice.util;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
    // Clave secreta para firmar el JWT
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Tiempo de expiración del token en milisegundos
    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        // Obtener el usuario autenticado
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        
        // Obtener los roles del usuario y unirlos en una cadena
        String roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // Construir el JWT
        return Jwts.builder()

                // Establecer el sujeto del JWT como el nombre de usuario
                .setSubject(userPrincipal.getUsername())
                // Establecer los roles como una reclamación personalizada
                .claim("roles", roles)
                // Establecer la fecha de emisión y la fecha de expiración
                .setIssuedAt(new Date())
                // Establecer la fecha de expiración del token
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                // Firmar el JWT con la clave secreta y el algoritmo de firma
                .signWith(key(), SignatureAlgorithm.HS256)
                // Construir el JWT y convertirlo a una cadena
                .compact();
    }

    // Método para obtener la clave secreta como un objeto Key 
    private Key key() {
        // Decodificar la clave secreta desde Base64 y crear un objeto Key
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // Métodos para extraer información del JWT
    public String getUserNameFromJwtToken(String token) {

        // Analizar el token JWT y obtener el cuerpo de las reclamaciones 
        return Jwts.parserBuilder()
                // Establecer la clave de firma para verificar el token
                .setSigningKey(key())
                // Construir el analizador y extraer el sujeto (nombre de usuario)
                .build()
                // Analizar el token y obtener el cuerpo de las reclamaciones
                .parseClaimsJws(token)
                // Obtener el sujeto del cuerpo de las reclamaciones, que es el nombre de usuario
                .getBody()
                // Retornar el nombre de usuario del sujeto
                .getSubject();
    }

    // Método para obtener los roles del usuario desde el JWT
    public String getRolesFromJwtToken(String token) {
        // Analizar el token JWT y obtener el cuerpo de las reclamaciones
        return Jwts.parserBuilder()
                // Establecer la clave de firma para verificar el token
                .setSigningKey(key())
                // Construir el analizador y extraer el cuerpo de las reclamaciones
                .build()
                // Analizar el token y obtener el cuerpo de las reclamaciones
                .parseClaimsJws(token)
                // Obtener el cuerpo de las reclamaciones
                .getBody()
                // Retornar los roles del usuario desde las reclamaciones
                .get("roles", String.class);
    }

    public boolean validateJwtToken(String authToken) {
        try {
            // Analizar el token JWT para verificar su validez
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            System.err.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }
}