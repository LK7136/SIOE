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

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CsrfException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    @Lazy
    private UtilisateurDetailService utilisateurDetailService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // AJOUT : autorise l'affichage du PDF dans l'iframe (même domaine)
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        // Ressources publiques : CSS, JS, images, logo de base
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/assets/**").permitAll()
                        // nimporte qui peut se login
                        .requestMatchers("/login").permitAll()

                        // seuls l'admin et l'organisateur ont accès à la page de paramètres
                        .requestMatchers("/parametres/liste").hasAnyRole("ADMIN", "ORGANISATEUR")
                        .requestMatchers(HttpMethod.POST, "/parametres").hasAnyRole("ADMIN", "ORGANISATEUR")
                        // Logo lu depuis la BDD : visible par tous
                        .requestMatchers("/parametres/logo", "/parametres/uploads/**").permitAll()

                        // suppression
                        .requestMatchers("/*/delete/**").hasRole("ADMIN")

                        // édition et ajout
                        .requestMatchers("/*/ajouter").hasAnyRole("ADMIN", "ORGANISATEUR")
                        .requestMatchers(HttpMethod.POST, "/*/edit").hasAnyRole("ADMIN", "ORGANISATEUR")

                        // création de rôles réservée à l'admin
                        .requestMatchers(HttpMethod.POST, "/roles").hasRole("ADMIN")

                        // accès au back office réservé aux admins
                        .requestMatchers("/festival/**").hasRole("ADMIN")
                        // reste des ressources : utilisateur authentifié
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout.logoutSuccessUrl("/login?logout").deleteCookies("JSESSIONID").permitAll())
                // session expirée : retour au login avec un message explicite
                .sessionManagement(s -> s.invalidSessionUrl("/login?expired"))
                // accès refusé : journalisation puis page 403
                .exceptionHandling(e -> e.accessDeniedHandler(accessDeniedHandler()));

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

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, exception) -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean connecte = auth != null && auth.isAuthenticated()
                    && !(auth instanceof AnonymousAuthenticationToken);

            if (exception instanceof CsrfException) {
                if (!connecte) {
                    // jeton CSRF perdu avec la session : l'utilisateur doit se reconnecter
                    log.warn("Jeton CSRF invalide, session expirée : {} {}",
                            request.getMethod(), request.getRequestURI());
                    response.sendRedirect(request.getContextPath() + "/login?expired");
                    return;
                }
                log.warn("Jeton CSRF invalide ou expiré : utilisateur {}, {} {}",
                        auth.getName(), request.getMethod(), request.getRequestURI());
                request.setAttribute("motifRefus", "csrf");
            } else {
                log.warn("Accès refusé : utilisateur {}, rôles {}, {} {}",
                        connecte ? auth.getName() : "anonyme",
                        connecte ? auth.getAuthorities() : "[]",
                        request.getMethod(), request.getRequestURI());
            }
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        };
    }
}