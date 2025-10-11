package com.arthurrouelle.demo.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class SecurityConfig {

  @Value("${app.jwt.secret}") private String secretBase64;

  /** UserDetailsService en mémoire (doc : InMemoryUserDetailsManager) */
  @Bean
  UserDetailsService userDetailsService(PasswordEncoder encoder) {
    var user = User.withUsername("admin@spring-boot-angular.com")
        .password(encoder.encode("secret"))
        .roles("ADMIN")
        .build();
    return new InMemoryUserDetailsManager(user);
  }

  /** PasswordEncoder Bcrypt (doc officielle : BCryptPasswordEncoder) */
  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(); // défaut: strength 10
  }

  /** Provider d’authentification username/password basé sur UserDetailsService */
  @Bean
  AuthenticationProvider authenticationProvider(UserDetailsService uds, PasswordEncoder enc) {
    var p = new DaoAuthenticationProvider(uds);
    p.setPasswordEncoder(enc);
    return p;
  }

  /** Récupère l’AuthenticationManager standard construit par Spring */
  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
    return cfg.getAuthenticationManager();
  }

  /** JwtEncoder (émet des JWT signés HS256) */
  @Bean
  JwtEncoder jwtEncoder() {
    byte[] secret = Base64.getDecoder().decode(secretBase64);
    var key = new SecretKeySpec(secret, "HmacSHA256");
    var jwkSource = new ImmutableSecret<SecurityContext>(key);
    return new NimbusJwtEncoder(jwkSource); // API officielle JwtEncoder/NimbusJwtEncoder
  }

  /** JwtDecoder (valide les Bearer tokens entrants, HS256) */
  @Bean
  JwtDecoder jwtDecoder() {
    byte[] secret = Base64.getDecoder().decode(secretBase64);
    var key = new SecretKeySpec(secret, "HmacSHA256");
    return NimbusJwtDecoder.withSecretKey(key).build(); // doc: withSecretKey(...)
  }

  /** Chaîne de filtres HTTP (SecurityFilterChain) */
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable()) // API stateless
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(auth -> auth
          .requestMatchers("/hello", "/api/auth/login").permitAll()
          .anyRequest().authenticated()
      )
      // Active la lecture des Authorization: Bearer <JWT>
      .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

    return http.build();
  }
}

