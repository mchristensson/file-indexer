package org.se.mac.blorksandbox.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class ApplicationConfig {
    /**
     * Custom implementation of the handler for incoming REST-calls.
     * Utilized by the Spring AuthenticationProvider
     *
     * @return A service reference
     */
    @Bean
    public UserDetailsService userDetailsService() {
        var svc = new InMemoryUserDetailsManager();
        UserDetails user = User.builder()
                .username("bob")
                .password("bob")
                .authorities("manager")
                .build();
        svc.createUser(user);
        return svc;
    }

    /**
     * Defines the password-encoder that verifies passwords
     * Utilized by the Spring AuthenticationProvider
     *
     * @return A passwordencoder mechanism
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
