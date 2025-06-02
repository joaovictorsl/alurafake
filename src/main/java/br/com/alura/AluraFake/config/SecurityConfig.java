package br.com.alura.AluraFake.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Task creation endpoints (INSTRUCTOR only)
                .requestMatchers("/task/new/**").hasRole("INSTRUCTOR")
                // Course creation and publication (INSTRUCTOR only)
                .requestMatchers("/course/new", "/course/*/publish").hasRole("INSTRUCTOR")
                // User creation (anyone)
                .requestMatchers("/user/new").anonymous()
                // All other endpoints: any authenticated user
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .permitAll()
            )
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PlainPasswordEncoder();
    }
}
