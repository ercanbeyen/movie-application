package com.ercanbeyen.movieapplication.security;

import com.ercanbeyen.movieapplication.constant.enums.RoleName;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain configure(final HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.cors().and()
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/registration", "/login").permitAll()
                .requestMatchers("/api/v1/audience/{id}/roles", "/api/v1/audiences").hasAnyAuthority(RoleName.ADMIN.name())
                .requestMatchers("/api/v1/audiences/**").hasAnyAuthority(RoleName.USER.name())
                .requestMatchers(HttpMethod.GET, "/api/v1/movies/**", "/api/v1/directors/**", "/api/v1/actors/**", "/api/v1/cinemas/**", "/api/v1/audiences/**").hasAnyAuthority(RoleName.USER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/movies/**", "/api/v1/directors/**", "/api/v1/actors/**", "/api/v1/cinemas/**").hasAnyAuthority(RoleName.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/api/v1/movies/**", "/api/v1/directors/**", "/api/v1/actors/**", "/api/v1/cinemas/**").hasAnyAuthority(RoleName.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, "/api/v1/movies/**", "/api/v1/directors/**", "/api/v1/actors/**", "/api/v1/cinemas/**").hasAnyAuthority(RoleName.ADMIN.name())
                .requestMatchers("/api/v1/roles/**", "/api/v1/ratings/**").hasAnyAuthority(RoleName.ADMIN.name())
                .anyRequest().authenticated()
                .and()
                .formLogin().defaultSuccessUrl("/api/v1/movies", true)
                .loginPage("/login")
                .and()
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .invalidSessionUrl("/logout?expired")
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)

                )
                .logout(logout -> logout.deleteCookies("JSESSIONID").invalidateHttpSession(true))
                .build();
    }
}
