package ru.kabachok.abobus.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseTool {

    private static final String DB_NAME = property("abobus.db.name", "ABOBUS_DB_NAME", "abobus");
    private static final String HOST = property("abobus.db.host", "ABOBUS_DB_HOST", "localhost");
    private static final String PORT = property("abobus.db.port", "ABOBUS_DB_PORT", "5432");
    private static final String USER = property("abobus.db.user", "ABOBUS_DB_USER", "postgres");
    private static final String PASSWORD = property("abobus.db.password", "ABOBUS_DB_PASSWORD", "123456789");

    public static void main(String[] args) throws Exception {
        String command = args.length == 0 ? "init" : args[0];
        switch (command) {
            case "create" -> createDatabase();
            case "init" -> initDatabase();
            case "print" -> printDatabase();
            case "drop" -> dropDatabase();
            default -> throw new IllegalArgumentException("Unknown command: " + command);
        }
    }

    private static void initDatabase() throws Exception {
        createDatabase();
        try (Connection connection = connect(DB_NAME)) {
            if (tableExists(connection, "company")) {
                syncSequences(connection);
                System.out.println("Database " + DB_NAME + " already contains schema");
                return;
            }
            executeSqlFile(connection, Path.of("sql", "create.sql"));
            executeSqlFile(connection, Path.of("sql", "insert.sql"));
            syncSequences(connection);
            System.out.println("Database " + DB_NAME + " initialized");
        }
    }

    private static void createDatabase() throws SQLException {
        validateDatabaseName(DB_NAME);
        try (Connection connection = connect("postgres");
             Statement statement = connection.createStatement()) {
            if (databaseExists(statement, DB_NAME)) {
                System.out.println("Database " + DB_NAME + " already exists");
                return;
            }
            statement.executeUpdate("CREATE DATABASE " + DB_NAME);
            System.out.println("Database " + DB_NAME + " created");
        }
    }

    private static void printDatabase() throws SQLException {
        try (Connection connection = connect(DB_NAME);
             Statement statement = connection.createStatement()) {
            printCount(statement, "company");
            printCount(statement, "stop");
            printCount(statement, "route");
            printCount(statement, "route_stop");
            printCount(statement, "route_fare");
            printCount(statement, "trip");
            printCount(statement, "trip_stop_time");
            printCount(statement, "client");
            printCount(statement, "orders");
        }
    }

    private static void dropDatabase() throws SQLException {
        validateDatabaseName(DB_NAME);
        try (Connection connection = connect("postgres");
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    SELECT pg_terminate_backend(pid)
                    FROM pg_stat_activity
                    WHERE datname = '%s' AND pid <> pg_backend_pid()
                    """.formatted(DB_NAME));
            statement.executeUpdate("DROP DATABASE IF EXISTS " + DB_NAME);
            System.out.println("Database " + DB_NAME + " dropped");
        }
    }

    private static Connection connect(String databaseName) throws SQLException {
        String url = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + databaseName;
        return DriverManager.getConnection(url, USER, PASSWORD);
    }

    private static boolean databaseExists(Statement statement, String databaseName) throws SQLException {
        try (ResultSet rs = statement.executeQuery(
                "SELECT 1 FROM pg_database WHERE datname = '" + databaseName + "'")) {
            return rs.next();
        }
    }

    private static boolean tableExists(Connection connection, String tableName) throws SQLException {
        try (ResultSet rs = connection.getMetaData().getTables(null, "public", tableName, null)) {
            return rs.next();
        }
    }

    private static void executeSqlFile(Connection connection, Path path) throws IOException, SQLException {
        String sql = Files.readString(path, StandardCharsets.UTF_8);
        for (String statementText : sql.split(";")) {
            String statement = statementText.trim();
            if (!statement.isBlank()) {
                try (Statement jdbcStatement = connection.createStatement()) {
                    jdbcStatement.execute(statement);
                }
            }
        }
    }

    private static void printCount(Statement statement, String tableName) throws SQLException {
        try (ResultSet rs = statement.executeQuery("SELECT count(*) FROM " + tableName)) {
            rs.next();
            BigDecimal count = rs.getBigDecimal(1);
            System.out.println(tableName + ": " + count);
        }
    }

    private static void syncSequences(Connection connection) throws SQLException {
        syncSequence(connection, "company_id_seq", "company");
        syncSequence(connection, "stop_id_seq", "stop");
        syncSequence(connection, "route_id_seq", "route");
        syncSequence(connection, "route_stop_id_seq", "route_stop");
        syncSequence(connection, "route_fare_id_seq", "route_fare");
        syncSequence(connection, "trip_id_seq", "trip");
        syncSequence(connection, "trip_stop_time_id_seq", "trip_stop_time");
        syncSequence(connection, "client_id_seq", "client");
        syncSequence(connection, "orders_id_seq", "orders");
    }

    private static void syncSequence(Connection connection, String sequenceName, String tableName) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    SELECT setval('%s', COALESCE((SELECT MAX(id) FROM %s), 1), true)
                    """.formatted(sequenceName, tableName));
        }
    }

    private static String property(String propertyName, String envName, String defaultValue) {
        String property = System.getProperty(propertyName);
        if (property != null && !property.isBlank()) {
            return property;
        }
        String env = System.getenv(envName);
        if (env != null && !env.isBlank()) {
            return env;
        }
        return defaultValue;
    }

    private static void validateDatabaseName(String databaseName) {
        if (!databaseName.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            throw new IllegalArgumentException("Unsafe database name: " + databaseName);
        }
    }
}
