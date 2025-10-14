package co.com.crediya.r2dbc.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

class PostgreSQLConnectionPoolTest {

    @Test
    void getConnectionConfig() throws Exception {
        PostgreSQLConnectionPool pool = new PostgreSQLConnectionPool();

        // Set dbHost via reflection if needed (not used in current method)
        Field dbHostField = PostgreSQLConnectionPool.class.getDeclaredField("dbHost");
        dbHostField.setAccessible(true);
        dbHostField.set(pool, "localhost");

        PostgresqlConnectionProperties properties = new PostgresqlConnectionProperties();
        properties.setHost("localhost");
        properties.setPort(5432);
        properties.setDatabase("testdb");
        properties.setSchema("public");
        properties.setUsername("testuser");
        properties.setPassword("testpass");

        Assertions.assertNotNull(pool.getConnectionConfig(properties));
    }
}
