package com.mangxahoi.mangxahoi_backend.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(
                    "/api/chinh-sach/moi-nhat",
                    "/api/nguoi-dung/**",
                    "/api/nguoi-dung/dang-ky", 
                    "/api/nguoi-dung/dang-nhap", 
                    "/api/nguoi-dung/xac-thuc",
                    "/api/nguoi-dung/dat-lai-mat-khau",
                    "/api/nguoi-dung/{id}",
                    "/{id}/anh-dai-dien",
                    "/api/nguoi-dung-anh/**",
                    "/api/auth/**",
                    "/api/quen-mat-khau/**",
                    "/api/xac-thuc/**",
                    "/api/goi-y/**",
                    "/tao-goi-y/{id}",
                    "/api/bai-viet/**",
                    "/api/saved-posts/**",
                    "/api/binh-luan/**",
                    "/api/ket-ban/**",
                    "/{idNguoiDung}",
                    "/api/thong-bao/**",
                    "/api/tinnhan/**",
                    "/api/admin/**",
                    "/api/bao-cao/guibaocao",
                    "/ws/chat/**",
                    "ws/chat/info/**",
                    "/ws/chat",
                    "/api/test/**"

                ).permitAll()
                .requestMatchers("/api/admin/**").hasRole("quan_tri_vien")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 