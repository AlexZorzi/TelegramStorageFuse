package com.alles.telegramstoragefuse;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class H2Create {

    private static final String createTableSQL = "create table Files\n" +
            "(\n" +
            "\tFileID longtext null,\n" +
            "\tFilename varchar null,\n" +
            "\tPath varchar null,\n" +
            "\tPathFull varchar null,\n" +
            "\tByteSize BIGINT null\n" +
            ");\n" +
            "\n";

    public static void main(String[] argv) throws SQLException {
        H2Create createTableExample = new H2Create();
        createTableExample.createTable();
    }

    public void createTable() throws SQLException {

        System.out.println(createTableSQL);
        // Step 1: Establishing a Connection
        try (Connection connection = H2JDBCUtils.getConnection();
             // Step 2:Create a statement using connection object
             Statement statement = connection.createStatement();) {

            // Step 3: Execute the query or update query
            statement.execute(createTableSQL);

        } catch (SQLException e) {
            // print SQL exception information
            H2JDBCUtils.printSQLException(e);
        }
    }
}