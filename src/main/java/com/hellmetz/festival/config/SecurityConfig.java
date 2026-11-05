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

    @Bean  // Ce Bean remplace votre AuthenticationFilter
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Ressources publiques : CSS, JS, images
                        .requestMatchers("/css/**", "/js/**").permitAll()
                        // nimporte qui peut se login
                        .requestMatchers("/login").permitAll()



                        //seule l'admin est organisateur ont accès a la page de parametres
                        .requestMatchers("/parametres/liste").hasAnyRole("ADMIN", "ORGANISATEUR")


                        //suppression
                        .requestMatchers("/*/delete/**").hasRole("ADMIN")

                        //edition et ajout
                        .requestMatchers("/*/ajouter").hasAnyRole("ADMIN", "ORGANISATEUR")
                        .requestMatchers(HttpMethod.POST, "/*/edit").hasAnyRole("ADMIN", "ORGANISATEUR")

                        //acces a la creation de roles que pour l'admin
                        .requestMatchers(HttpMethod.POST, "/roles").hasRole("ADMIN")


                        // acces au back office que pr les admins
                        .requestMatchers("/festival/**").hasRole("ADMIN")
                        // acces au reste des ressource que si on est authentifier
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")      // Votre page de login Thymeleaf
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll());

        return http.build();
    }

    // Ce Bean remplace votre usage direct de BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // A vous : implémentez UserDetailsService pour charger vos Utilisateur

    @Bean
    public DaoAuthenticationProvider chargerUtilisateur() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(utilisateurDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
