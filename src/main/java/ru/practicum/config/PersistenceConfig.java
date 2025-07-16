package ru.practicum.config;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Properties;

@EnableJpaRepositories(basePackages = "ru.practicum")
@Configuration
@RequiredArgsConstructor
public class PersistenceConfig {
    private final Environment environment;

    @Bean
    public DataSource dataSource() throws SQLException, IOException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(environment.getRequiredProperty("jdbc.driverClassName"));
        dataSource.setUrl(environment.getRequiredProperty("jdbc.url"));
        dataSource.setUsername(environment.getRequiredProperty("jdbc.username"));
        dataSource.setPassword(environment.getRequiredProperty("jdbc.password"));

        //не работает инициализация из коробки
        //перепробованы разные варианты
        String content = Files.readString(Path.of("./src/main/resources/schema.sql"));
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute(content);
        return dataSource;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.jdbc.time_zone",
                environment.getRequiredProperty("hibernate.jdbc.time_zone"));
        properties.put("hibernate.show_sql",
                environment.getProperty("hibernate.show_sql", "false"));
        properties.put("spring.jpa.hibernate.ddl-auto",
                environment.getProperty("spring.jpa.hibernate.ddl-auto", "none"));
        properties.put("spring.datasource.initialization-mode",
                environment.getProperty("spring.datasource.initialization-mode", "always"));
        properties.put("spring.sql.init.mode",
                environment.getProperty("spring.sql.init.mode", "always"));
        return properties;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        final HibernateJpaVendorAdapter vendorAdapter =
                new HibernateJpaVendorAdapter();

        final LocalContainerEntityManagerFactoryBean emf =
                new LocalContainerEntityManagerFactoryBean();

        emf.setDataSource(dataSource);
        emf.setJpaVendorAdapter(vendorAdapter);
        emf.setPackagesToScan("ru.practicum");
        emf.setJpaProperties(hibernateProperties());

        return emf;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}