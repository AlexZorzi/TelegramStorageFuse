package com.alles.telegramstoragefuse;


import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Select PreparedStatement JDBC Example
 *
 * @author Ramesh Fadatare
 *
 */
public class H2Select {
    private static final String QUERY = "select * from Files";

    public static ArrayList<DbEntry> SelectAll() {

        // using try-with-resources to avoid closing resources (boiler plate code)

        // Step 1: Establishing a Connection
        try (Connection connection = H2JDBCUtils.getConnection();

             // Step 2:Create a statement using connection object
             PreparedStatement preparedStatement = connection.prepareStatement(QUERY);) {
            System.out.println(preparedStatement);
            // Step 3: Execute the query or update query
            ResultSet rs = preparedStatement.executeQuery();

            // Step 4: Process the ResultSet object.
            ArrayList<DbEntry> Returnable = new ArrayList<>();
            while (rs.next()) {

                String Filename = rs.getString("Filename");
                String Path = rs.getString("Path");
                String PathFull = rs.getString("PathFull");
                String FileID = rs.getString("FileID");
                String ByteSize = rs.getString("ByteSize");
                Returnable.add(new DbEntry(Filename, Path, PathFull, FileID, ByteSize));
            }
            return Returnable;
        } catch (SQLException e) {
            H2JDBCUtils.printSQLException(e);
            return null;
        }
        // Step 4: try-with-resource statement will auto close the connection.
    }

    public static DbEntry Select(String PathFull) {

        String query = "SELECT * FROM Files WHERE PathFull = '"+PathFull+"';";

        try (Connection connection = H2JDBCUtils.getConnection();

             // Step 2:Create a statement using connection object
             PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            System.out.println(preparedStatement);
            // Step 3: Execute the query or update query
            ResultSet rs = preparedStatement.executeQuery();

            // Step 4: Process the ResultSet object.
            DbEntry Returnable = null;
            if (rs != null){
                rs.next();
                String Filename = rs.getString("Filename");
                String Path = rs.getString("Path");
                String Path_Full = rs.getString("PathFull");
                String FileID = rs.getString("FileID");
                String ByteSize = rs.getString("ByteSize");
                Returnable = new DbEntry(Filename, Path, Path_Full, FileID, ByteSize);
            }
            return Returnable;
        } catch (SQLException e) {
            //H2JDBCUtils.printSQLException(e);
            return null;
        }
        // Step 4: try-with-resource statement will auto close the connection.
    }
}

class DbEntry {
    public String Filename;
    public String Path;
    public String PathFull;
    public String FileID;
    public String ByteSize;
    public InputStream FileData;

    public DbEntry(String Filename,String Path,String PathFull,String FileID,String ByteSize){
        this.Filename = Filename;
        this.FileID = FileID;
        this.Path = Path;
        this.PathFull = PathFull;
        this.ByteSize = ByteSize;
    }
}
