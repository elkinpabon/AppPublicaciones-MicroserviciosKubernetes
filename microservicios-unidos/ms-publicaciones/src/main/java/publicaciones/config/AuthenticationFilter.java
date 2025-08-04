package publicaciones.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import publicaciones.client.AuthServiceClient;
import publicaciones.dto.TokenValidationRequest;
import publicaciones.dto.TokenValidationResponse;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private AuthServiceClient authServiceClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
            FilterChain filterChain) throws ServletException, IOException {

        String jwt = parseJwt(request);
        String method = request.getMethod();
        String path = request.getRequestURI();

        // Si es GET, NO validar JWT (público)
        if ("GET".equals(method) || path.startsWith("/actuator") || path.startsWith("/h2-console")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Para POST, PUT, DELETE validar JWT
        if (jwt != null && StringUtils.hasText(jwt)) {
            try {
                TokenValidationRequest validationRequest = new TokenValidationRequest();
                validationRequest.setToken(jwt);

                TokenValidationResponse validationResponse = authServiceClient.validateToken(validationRequest);
                
                if (validationResponse != null && validationResponse.isValid()) {
                    List<GrantedAuthority> authorities = Arrays.stream(validationResponse.getRoles().split(","))
                            .map(role -> new SimpleGrantedAuthority(role.trim()))
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                    validationResponse.getUsername(),
                                    null,
                                    authorities
                            );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}