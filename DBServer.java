import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

class DBConnect{
    Connection con;
    Statement st;
    public DBConnect(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/poll_app?characterEncoding=utf8", "root", "");
            st = con.createStatement();

        }
        catch (Exception e) {
          System.out.println("Error "+e);
       }
    }

}
class ClientHandler extends Thread{
    private Socket clientSocket;
    private DBConnect dbcon;
    public ClientHandler(Socket clientSocket, DBConnect dbcon) {
        this.clientSocket = clientSocket;
        this.dbcon = dbcon;
    }
    public void run() {
        try{
            DataInputStream sin = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream sout = new DataOutputStream(clientSocket.getOutputStream());
            String str;
            while(true){
                str = sin.readUTF();
                if(str.equals("insert")){
                    str = sin.readUTF();
                    System.out.println(str);
                    dbcon.st.executeUpdate(str);
                }
                if(str.equals("select")){
                    str = sin.readUTF();
                    System.out.println(str);
                    ResultSet rs = dbcon.st.executeQuery(str);
                    if(rs.next())
                        sout.writeBoolean(true);
                    else
                        sout.writeBoolean(false);
                }
            }

        } catch (IOException  | SQLException e) {
            System.out.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}

public class DBServer{
    
    public static void main(String[] args) {
        String str;
        ServerSocket ss;
        try {
            ss = new ServerSocket(1234);
            DBConnect dbcon = new DBConnect();
            while (true) {
                Socket clientSocket = ss.accept();
                System.out.println("Client connected: " + clientSocket);
                ClientHandler clientHandler = new ClientHandler(clientSocket, dbcon);
                clientHandler.start();
            }
        }
         catch (Exception e) {
           System.out.println("Error "+e);
        }
    }
}