package com.arthurrouelle.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/players/**")
                        .hasRole("PLAYER"))
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
        User.UserBuilder users = User.builder();
        UserDetails isis = users
                .username("isis")
                .password(passwordEncoder.encode("abc123"))
                .roles("PLAYER")
                .build();
        UserDetails pipoune = users
                .username("pipoune")
                .password(passwordEncoder.encode("efg123"))
                .roles("PLAYER")
                .build();
        UserDetails notAPlayer = users
                .username("not-a-player")
                .password(passwordEncoder.encode("qrs456"))
                .roles("NO-PLAYER")
                .build();
        UserDetails luna = users
                .username("luna")
                .password(passwordEncoder.encode("xyz789"))
                .roles("PLAYER")
                .build();
        return new InMemoryUserDetailsManager(isis, pipoune, notAPlayer, luna);
    }
}