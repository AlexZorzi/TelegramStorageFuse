package com.alles.telegramstoragefuse;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Insert PrepareStatement JDBC Example
 *
 * @author Ramesh Fadatare
 *
 */
public class H2Insert {
    private static final String INSERT_USERS_SQL = "INSERT INTO Files" +
            "  ( Filename, Path, PathFull, FileID, ByteSize) VALUES " +
            " ( ?, ?, ?, ?, ?);";

    public static void insertRecord( String Filename, String Path,String PathFull,String FileID,String ByteSize) {
        System.out.println(INSERT_USERS_SQL);
        // Step 1: Establishing a Connection
        try (Connection connection = H2JDBCUtils.getConnection();
             // Step 2:Create a statement using connection object
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL)) {
            preparedStatement.setString(1, Filename);
            preparedStatement.setString(2, Path);
            preparedStatement.setString(3, PathFull);
            preparedStatement.setString(4, FileID);
            preparedStatement.setString(5, ByteSize);

            System.out.println(preparedStatement);
            // Step 3: Execute the query or update query
            preparedStatement.executeUpdate();
        } catch (SQLException e) {

            // print SQL exception information
            H2JDBCUtils.printSQLException(e);
        }

        // Step 4: try-with-resource statement will auto close the connection.
    }
}