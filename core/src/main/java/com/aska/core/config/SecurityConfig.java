package com.aska.core.config;

import com.aska.core.security.CustomAccessDeniedHandler;
import com.aska.core.security.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, KeycloakJwtConverter jwtConverter) throws Exception {

        http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .csrf(csrf -> csrf.disable())
           /* .csrf(csrf -> csrf.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                    .ignoringRequestMatchers("/api/auth/register", "/api/auth/login")
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class) */

            .authorizeHttpRequests(requests -> requests
                    .requestMatchers("/error").permitAll()
                    .requestMatchers("/api/**").authenticated()
                    .anyRequest().denyAll())
            .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter))
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
            );
        return http.build();
    }
}
