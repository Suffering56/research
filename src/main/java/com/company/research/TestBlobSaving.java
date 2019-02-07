package com.company.research;

import java.io.IOException;
import java.sql.*;
import java.util.Base64;

public class TestBlobSaving {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        test();
    }

    //"jdbc:postgresql://localhost:5432/shelter","shelter", "shelter");
    //"jdbc:postgresql://172.18.68.14:5432/shelter","shelter_dev", "whargarbl");

    public static void test() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/research",
                "postgres",
                "postgres"
        )) {
            PreparedStatement statement = connection.prepareStatement("update game_data set data=? where id = 1");
//            PreparedStatement statement = connection.prepareStatement("update game_data set description=? where id = 1");

            try {
                connection.setAutoCommit(false);
                ExtendedByteArrayOutputStream out = new ExtendedByteArrayOutputStream();
                out.write("Hello world3".getBytes());
//                out.close();
//                out.flush();
                statement.setBinaryStream(1, out.createInputStream());
//                statement.setBytes(1,"Hello world".getBytes());
//                statement.setString(1,"description2");
                statement.executeUpdate();


                connection.commit();
                statement.close();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
                connection.rollback();
            } finally {
                connection.setAutoCommit(true);
                showData(connection.createStatement(), "select * from game_data", 3);

                connection.close();
            }

        }
    }



    private static void showData(Statement statement, String query, int columns) throws SQLException {
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            for (int i = 0; i < columns; i++) {
                int columnIndex = i + 1;
                System.out.println(resultSet.getObject(columnIndex));
            }
        }
    }
}


//    public boolean exists() {
//        SecurityManager security = System.getSecurityManager();
//        if (security != null) {
//            security.checkRead(path);
//        }
//        if (isInvalid()) {
//            return false;
//        }
//        return ((fs.getBooleanAttributes(this) & FileSystem.BA_EXISTS) != 0);
//    }