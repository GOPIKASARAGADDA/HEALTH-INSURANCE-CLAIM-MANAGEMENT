package com.genc.healthinsurance.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
 
	@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
        .headers(headers -> headers
                   .frameOptions(frame -> frame.sameOrigin()) // allows embedding from same origin
               )

        .csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth.anyRequest().permitAll()).formLogin(login -> login.disable()).logout(logout -> logout.disable());
		return http.build();
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}