package com.hellmetz.festival.config;

import com.hellmetz.festival.service.UtilisateurDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    @Lazy
    private UtilisateurDetailService utilisateurDetailService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth

                        // Ressources publiques : CSS, JS, images
                        .requestMatchers("/css/**", "/js/**").permitAll()

                        // Permet l'accès aux pages d'erreur générées par Spring Boot
                        .requestMatchers("/error", "/error/**").permitAll()

                        // nimporte qui peut se login
                        .requestMatchers("/login").permitAll()

                        // seule l'admin et organisateur ont accès à la page de paramètres
                        .requestMatchers("/parametres/liste").hasAnyRole("ADMIN", "ORGANISATEUR")

                        // suppression
                        .requestMatchers("/*/delete/**").hasRole("ADMIN")

                        // édition et ajout
                        .requestMatchers("/*/ajouter").hasAnyRole("ADMIN", "ORGANISATEUR")
                        .requestMatchers(HttpMethod.POST, "/*/edit").hasAnyRole("ADMIN", "ORGANISATEUR")

                        // accès à la création de rôles uniquement pour l'admin
                        .requestMatchers(HttpMethod.POST, "/roles").hasRole("ADMIN")

                        // accès au back office uniquement pour les admins
                        .requestMatchers("/festival/**").hasRole("ADMIN")

                        // accès au reste des ressources uniquement si authentifié
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider chargerUtilisateur() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(utilisateurDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}