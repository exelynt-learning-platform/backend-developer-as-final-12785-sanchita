package com.Sanchita.Resource_Booking_System.Config;


import com.Sanchita.Resource_Booking_System.Security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/auth/register","/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/resource-types/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/resource-types/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/resources/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/resources/**").hasRole("ADMIN")
                        .requestMatchers("/reservation/request/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/reservation/**").hasRole("ADMIN")
                        .anyRequest()
                        .authenticated()
                )
                        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }


}
