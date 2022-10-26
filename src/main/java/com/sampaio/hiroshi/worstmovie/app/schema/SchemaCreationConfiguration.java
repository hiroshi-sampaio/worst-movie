package com.sampaio.hiroshi.worstmovie.app.schema;

import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

@Slf4j
@Configuration
public class SchemaCreationConfiguration {

    @Bean
    public ConnectionFactoryInitializer databaseSchemaCreator(ConnectionFactory connectionFactory) {
        var initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("worst-movie-schema.sql")));
        log.debug("Database schema created");
        return initializer;
    }
}
