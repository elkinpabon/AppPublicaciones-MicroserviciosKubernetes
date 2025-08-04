package publicaciones.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AuthenticationFilter authFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // USAR SOLO AntPathRequestMatcher para evitar conflictos
                
                // H2 Console - SIEMPRE público
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                
                // Actuator - SIEMPRE público
                .requestMatchers(new AntPathRequestMatcher("/actuator/**")).permitAll()
                
                // ENDPOINTS PÚBLICOS (GET) - SIN AUTENTICACIÓN
                .requestMatchers(new AntPathRequestMatcher("/api/**", HttpMethod.GET.name())).permitAll()
                
                // ENDPOINTS PROTEGIDOS (POST, PUT, DELETE)
                .requestMatchers(new AntPathRequestMatcher("/api/**", HttpMethod.POST.name())).hasAuthority("ROLE_ADMIN")
                .requestMatchers(new AntPathRequestMatcher("/api/**", HttpMethod.PUT.name())).hasAuthority("ROLE_ADMIN")
                .requestMatchers(new AntPathRequestMatcher("/api/**", HttpMethod.DELETE.name())).hasAuthority("ROLE_ADMIN")
                
                // Cualquier otra request
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions().disable())
            .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}