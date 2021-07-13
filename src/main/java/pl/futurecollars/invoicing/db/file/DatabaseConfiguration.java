package pl.futurecollars.invoicing.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.futurecollars.invoicing.db.file.FileBasedDatabase;
import pl.futurecollars.invoicing.db.file.IdService;
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase;
import pl.futurecollars.invoicing.db.sql.SqlDatabase;
import pl.futurecollars.invoicing.utils.FilesService;
import pl.futurecollars.invoicing.utils.JsonService;

@Configuration
@Slf4j
public class DatabaseConfiguration {

    @Bean
    public IdService idService(
        FilesService filesService,
        @Value("${invoicing-system.database.directory}") String databaseDirectory,
        @Value("${invoicing-system.database.id.file}") String idFile) throws IOException {
        Path idFilePath = Files.createTempFile(databaseDirectory, idFile);
        return new IdService(idFilePath, filesService);
    }

    @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "file")
    @Bean
    public Database fileBasedDatabase(
        IdService idService,
        FilesService filesService,
        JsonService jsonService,
        @Value("${invoicing-system.database.directory}") String databaseDirectory,
        @Value("${invoicing-system.database.invoices.file}") String invoicesFile)
        throws IOException {
        log.debug("Creating in-file database");
        Path databaseFilePath = Files.createTempFile(databaseDirectory, invoicesFile);
        return new FileBasedDatabase(databaseFilePath, idService, filesService, jsonService);
    }

    @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "memory")
    @Bean
    public Database inMemoryDatabase() {
        log.debug("Creating in-memory database");
        return new InMemoryDatabase();
    }

    @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "sql")
    @Bean
    public Database sqlDatabase(JdbcTemplate jdbcTemplate) {
        log.debug("Creating sql database");
        return new SqlDatabase(jdbcTemplate);
    }

}
