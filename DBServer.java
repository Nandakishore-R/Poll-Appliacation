import java.beans.Statement;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBServer{
    public static void main(String[] args) {
        Connection con;
        Statement st;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/poll_app?characterEncoding=utf8", "root", "");
        }
         catch (Exception e) {
           System.out.println("Error "+e);
        }
    }
}