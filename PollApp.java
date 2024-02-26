import java.io.*;
import java.net.*;
import java.util.*;
public class PollApp {
    public static int mainMenu(){
        Scanner c = new Scanner(System.in);
        int ch = 3;
        try {
            System.out.println("1 -> Login POLL");
            System.out.println("2 -> Signup POLL");
            System.out.println("3 -> QUIT");
            System.out.print("Enter your choice : ");
            ch = c.nextInt();
        } catch (Exception e) {
            System.out.println(e);
        }
        return ch;
    }

    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
               new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
            else {
               System.out.print("\033\143");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        Socket cs;
        DataInputStream sin;
        DataOutputStream sout;
        Scanner s = new Scanner(System.in);
        int ch;
        String uname, pass, email,  str;
        try {
            cs = new Socket("localhost",1234);
            sin = new DataInputStream(cs.getInputStream());
            sout = new DataOutputStream(cs.getOutputStream());
            for(ch = mainMenu();ch!=3;ch=mainMenu()){
                switch (ch) {
                    case 1:
                        System.out.print("Enter email : ");
                        email = s.nextLine();
                        System.out.print("Enter password : ");
                        pass = s.nextLine();
                        str = "select * from tbl_user where email='"+email+"' and password = '"+pass+"'";
                        sout.writeUTF("select");
                        sout.writeUTF(str);
                        if(sin.readBoolean()){
                            clearConsole();
                            System.out.println("Welcome ");
                        }
                        else
                        System.out.println("Wrong Details!!");
                        break;
                    case 2:
                        System.out.print("Enter username : ");
                        uname = s.nextLine();
                        System.out.print("Enter email : ");
                        email = s.nextLine();
                        System.out.print("Enter password : ");
                        pass = s.nextLine();
                        str = "select * from tbl_user where email='"+email+"'";
                        sout.writeUTF("select");
                        sout.writeUTF(str);
                        if(!sin.readBoolean()){
                            str = "insert into tbl_user(username, email, password) values('"+uname+"', '"+email+"','"+pass+"')";
                            sout.writeUTF("insert");
                            sout.writeUTF(str);
                            System.out.println("User Succesfully registered");
                        }
                        else
                            System.out.println("Email already exists!!!");
                        break;
                }
            }
            s.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
