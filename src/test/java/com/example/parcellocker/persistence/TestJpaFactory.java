package com.example.parcellocker.persistence;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Map;

public final class TestJpaFactory {

    private TestJpaFactory() {
    }

    public static EntityManagerFactory createH2InMemoryEntityManagerFactory(String databaseName) {
        return Persistence.createEntityManagerFactory(
                "parcel-lockerPU",
                Map.of(
                        "jakarta.persistence.jdbc.driver", "org.h2.Driver",
                        "jakarta.persistence.jdbc.url", "jdbc:h2:mem:" + databaseName + ";DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
                        "jakarta.persistence.jdbc.user", "sa",
                        "jakarta.persistence.jdbc.password", "",

                        "hibernate.dialect", "org.hibernate.dialect.H2Dialect",
                        "hibernate.hbm2ddl.auto", "create-drop",
                        "hibernate.show_sql", "false",
                        "hibernate.format_sql", "true"
                )
        );
    }

    public static EntityManagerFactory createPostgresEntityManagerFactory(PostgreSQLContainer<?> postgres) {
        return Persistence.createEntityManagerFactory(
                "parcel-lockerPU",
                Map.of(
                        "jakarta.persistence.jdbc.driver", "org.postgresql.Driver",
                        "jakarta.persistence.jdbc.url", postgres.getJdbcUrl(),
                        "jakarta.persistence.jdbc.user", postgres.getUsername(),
                        "jakarta.persistence.jdbc.password", postgres.getPassword(),

                        "hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect",
                        "hibernate.hbm2ddl.auto", "create-drop",
                        "hibernate.show_sql", "false",
                        "hibernate.format_sql", "true"
                )
        );
    }
}