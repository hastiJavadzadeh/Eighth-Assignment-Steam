package Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Import {
    public static void main(String args[]) throws FileNotFoundException {

        File file = new File("C:\\Users\\astan\\Eighth-Assignment-Steam\\src\\main\\java\\Server\\Resources");

        File[] files = file.listFiles();

        String url = "jdbc:postgresql://localhost:5432/Steam";
        String user = "postgres";
        String pass = "12345";

        try {
            Connection connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Connected to the PostgreSQL database!");

            Statement statement = connection.createStatement();

            for (File file1 : files){
                if (file1.getName().endsWith(".txt")){
                    Scanner r = new Scanner(file1);
                    String id = r.nextLine();
                    String path = ("C:\\Users\\astan\\Eighth-Assignment-Steam\\src\\main\\java\\Server\\Resources" + id + ".png");
                    String query = "'" + id + "'" + " ,";
                    while (r.hasNextLine()) {
                        query += ("'" + r.nextLine() + "'" + " ,");
                    }
                    query += ("'" + path + "'");

                    statement.executeUpdate("INSERT INTO games VALUES (" + query + ")");
                }
            }

            statement.close();
            connection.close();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
