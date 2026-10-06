package com.hellmetz.festival.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Garantit la fermeture propre du pool de connexions HikariCP a l'arret de
 * l'application. Spring ferme deja le DataSource automatiquement (Hikari
 * implemente Closeable), mais ce hook rend l'operation explicite et tracee
 * dans les logs : utile pour eviter de saturer les connexions de la VM.
 */
@Component
public class DataSourceShutdownConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceShutdownConfig.class);

    private final DataSource dataSource;

    public DataSourceShutdownConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PreDestroy
    public void fermerConnexions() {
        if (dataSource instanceof HikariDataSource hikari) {
            log.info("Arret de l'application : fermeture du pool '{}'...", hikari.getPoolName());
            if (!hikari.isClosed()) {
                hikari.close();
                log.info("Pool HikariCP ferme : toutes les connexions a la base ont ete liberees.");
            }
        }
    }
}
