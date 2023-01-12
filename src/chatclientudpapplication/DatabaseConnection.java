package chatclientudpapplication;

import java.sql.*;

public class DatabaseConnection {
    private static Connection connection;
    private static Statement statement;

    public static Connection getConnection() {
        return connection;
    }
    public static Statement getStatement() {
        return statement;
    }

    public DatabaseConnection(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatbase","root","");
            statement = connection.createStatement();
        }catch(Exception e){
            System.out.println(e);
        }
    }
    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/chatbase";
        String username = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Connected to the database!");
            Statement stmt=connection.createStatement();
            String query="SELECT * FROM client";
            ResultSet rs=stmt.executeQuery(query);
            while(rs.next())
            {
                System.out.print(rs.getString(1) +" ");
                System.out.print(rs.getString(2) +" ");
                System.out.print(rs.getString(3) +" ");
            }
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}
