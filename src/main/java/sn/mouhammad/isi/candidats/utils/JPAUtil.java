package sn.mouhammad.isi.candidats.utils;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {
    // Prevent instantiation
    private JPAUtil() {
    }

    private static final String PERSISTENCE_UNIT_NAME = "PERSISTENCE";
    private static EntityManagerFactory factory;

    public static EntityManagerFactory getEntityManagerFactory() {
        if (factory == null) {
            java.util.Map<String, String> properties = new java.util.HashMap<>();
            
            String dbHost = System.getenv("DB_HOST");
            if (dbHost == null) dbHost = "localhost";
            
            String dbUser = System.getenv("DB_USER");
            if (dbUser == null) dbUser = "postgres";
            
            String dbPassword = System.getenv("DB_PASSWORD");
            if (dbPassword == null) dbPassword = "root";

            properties.put("jakarta.persistence.jdbc.url", "jdbc:postgresql://" + dbHost + ":5432/candidats");
            properties.put("jakarta.persistence.jdbc.user", dbUser);
            properties.put("jakarta.persistence.jdbc.password", dbPassword);

            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
        }
        return factory;
    }

    public static void shutdown() {
        if (factory != null) {
            factory.close();
        }
    }
}
