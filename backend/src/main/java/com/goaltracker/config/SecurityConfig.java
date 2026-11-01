package com.goaltracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Spring Security sits in front of the servlet container purely for session-policy/CSRF posture
 * (stateless, no CSRF needed for a token API) and CORS. The actual per-request authentication
 * decision for every /api/** endpoint is made by {@link com.goaltracker.security.JwtAuthFilter},
 * a JAX-RS filter registered in Jersey's own chain (see {@link JerseyConfig}), since Jersey -
 * not Spring MVC - dispatches the resource methods. We therefore permit everything here and let
 * Jersey enforce authentication.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable());
        return http.build();
    }
}
