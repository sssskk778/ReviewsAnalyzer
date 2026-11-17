package com.aska.core.config;

import com.aska.core.filter.CsrfCookieFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AppUserDetailsService userDetailsService;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))

            .csrf(csrf -> csrf.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                    .ignoringRequestMatchers("/api/auth/register", "/api/auth/login")
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)

            .authorizeHttpRequests((requests) -> requests
                    .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                    .requestMatchers("/api/**").authenticated()
                    .anyRequest().permitAll())
            .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

            .logout(logout -> logout
                    .logoutUrl("/api/auth/logout")
                    .deleteCookies("JSESSIONID")
                    .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)));
        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
