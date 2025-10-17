package com.arthurrouelle.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("production")
public class DatabaseConfig {
    
    // Configuration spécifique SQLite 
    // Le problème de pagination sera résolu en modifiant les requêtes
}