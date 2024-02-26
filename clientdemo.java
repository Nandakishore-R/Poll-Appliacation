import java.io.*;
import java.net.*;
import java.util.*;

class clientdemo{
    public static void main(String[] args) {
        Socket cs;
        DataInputStream sin;
        DataOutputStream sout;
        Scanner s;
        try {
            cs = new Socket("localhost",1234);
            sin = new DataInputStream(cs.getInputStream());
            sout = new DataOutputStream(cs.getOutputStream());
            s = new Scanner(System.in);
            String str;
            do{
                System.out.println("Enter text to send : ");
                str = s.nextLine();
                sout.writeUTF(str);
            }while(!str.equals("quit"));
        } catch (Exception e) {}
    }
}
