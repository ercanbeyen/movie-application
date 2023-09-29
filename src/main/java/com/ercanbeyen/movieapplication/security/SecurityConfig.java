package com.ercanbeyen.movieapplication.security;

import com.ercanbeyen.movieapplication.constant.enums.RoleName;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
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
                .requestMatchers("/api/v1/register", "/api/v1/audiences", "/login").permitAll()
                //.requestMatchers("/**/register", "/**/audiences/**", "/login").anonymous()
                .requestMatchers("/api/v1/movies/**", "/api/v1/directors/**", "/api/v1/actors/**", "/api/v1/cinemas/**").hasAnyAuthority(RoleName.USER.name())
                .requestMatchers("/api/v1/roles").hasAnyAuthority(RoleName.ADMIN.name())
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
