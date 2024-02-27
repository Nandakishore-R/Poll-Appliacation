import java.io.*;
import java.net.*;
import java.sql.*;

class Poll{
    int poll_id;
    int participants[];
    public Poll(int id){
        poll_id = id;
    }
}

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
    private DatagramSocket ds;
    private DatagramPacket dp;
    private DBConnect dbcon;
    byte[] buffer = new byte[1000];
    public ClientHandler(DatagramSocket ds, DBConnect dbcon, DatagramPacket dp) {
        this.ds = ds;
        this.dbcon = dbcon;
        this.dp = dp;
    }
    public void run() {
        try{
            Socket cs;
            DataInputStream sin;
            DataOutputStream sout;
            cs = new Socket("localhost",1234);
            // sin = new DataInputStream(cs.getInputStream());
            sout = new DataOutputStream(cs.getOutputStream());

            String msg = new String(dp.getData()).trim();
            System.out.println(msg);
            String[] arr = msg.split(",",2);
            if(arr[0].equals("create")){
                System.out.println(dp.getPort());
                System.out.println(dp.getAddress());
                int id = Integer.parseInt(arr[1]);
                Poll p = new Poll(id);
                System.out.println(p.poll_id);
                String str = "insert into tbl_members(poll_id, owner) values("+p.poll_id+", "+1+")";
                sout.writeUTF("insert");
                sout.writeUTF(str);
            }
            else
                System.out.println(msg);
        }catch (
            // IOException | SQLException
             Exception e) {
            System.out.println("Error handling client: " + e.getMessage());
        }
    }
}
public class PollServer {
    public static void main(String[] args) {
        DatagramSocket ds = null;
        DatagramPacket dp = null;
        try {
            DBConnect dbcon = new DBConnect();
            ds = new DatagramSocket(4321);
            byte[] buffer = new byte[1000];
            while (true) {
                dp = new DatagramPacket(buffer, buffer.length);
                ds.receive(dp);
                ClientHandler clientHandler = new ClientHandler(ds, dbcon, dp);
                clientHandler.start();
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
