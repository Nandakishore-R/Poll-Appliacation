import java.io.*;
import java.net.*;
import java.util.*;
class Poll{
    int id;
    String question;
    String options[];
    public Poll(String q, String opt[]){
        this.question = q;
        this.options = opt;
    }
}
class TX extends Thread{
    DatagramSocket ds;
    // private volatile boolean isRunning = true;
    public TX(DatagramSocket ds){
        this.ds = ds;
    }
    public void run(DatagramSocket ds){
        byte[] buffer = new byte[1000];
        while (true) {
           DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
           try {
                ds.receive(dp);
                String msg = new String(dp.getData()).trim();

           } catch (Exception e) {
            System.out.println(e);
           }
           
        }
    }
    public void kill(){
        // isRunning = false;
    }
}

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

    public static int pollMenu(){
        Scanner c = new Scanner(System.in);
        int ch = 3;
        try {
            System.out.println("1 -> Create POLL");
            System.out.println("2 -> Join POLL");
            System.out.println("3 -> View POLL RESULT");
            System.out.println("4 -> QUIT");
            System.out.print("Enter your choice : ");
            ch = c.nextInt();
        } catch (Exception e) {
            System.out.println(e);
        }
        return ch;
    }

    public static void pollInterface(DataInputStream in, DataOutputStream out){
        int ch,n,poll_id;
        String q,options[],str;
        Scanner s = new Scanner(System.in);
        try {
            for(ch = pollMenu();ch!=4;ch=pollMenu()){
                switch (ch) {
                    case 1:
                        System.out.println("Enter Poll Question"); 
                        q = s.nextLine();
                        System.out.println("Enter No of Options"); 
                        n = s.nextInt();
                        s.nextLine();
                        options = new String[n];
                        for(int i=0;i<n;i++){
                            System.out.println("Enter option "+(i+1));
                            options[i] = s.nextLine();
                        }
                        Poll p = new Poll(q, options);
                        str = "insert into tbl_poll(poll_question, poll_type, password, status) values('"+q+"',  \"\",\"\","+1+")";
                        out.writeUTF("poll_insert");
                        out.writeUTF(str);
                        poll_id = in.readInt();
                        str = "create,"+poll_id;
                        DatagramSocket ds = new DatagramSocket();
                        sentToPollServer(poll_id,str,ds);
                        System.out.println("Id is "+poll_id);
                        for(int i=0;i<n;i++){
                            str = "insert into tbl_options(poll_id, option_value) values('"+poll_id+"', '"+options[i]+"')";
                            out.writeUTF("insert");
                            out.writeUTF(str);
                        }
                        break;
                    case 2:
                        System.out.println("Enter Poll Id"); 
                        poll_id = s.nextInt();
                        int i;
                        s.nextLine();
                        str = "select * from tbl_poll where poll_id='"+poll_id+"'";
                        out.writeUTF("select-poll");
                        out.writeUTF(str);
                        if(in.readBoolean()){
                            q = in.readUTF();
                            n = in.readInt();
                            options = new String[n];
                            for(i=0;i<n;i++){
                                options[i] = in.readUTF();
                            }
                            out.writeInt(0);
                           System.out.println("Poll found");
                           System.out.println(q);
                           System.out.println("Options");
                           for(i=0;i<n;i++){
                            System.out.println("Option "+(i+1)+" : "+options[i]);
                            }
                            do{
                                System.out.println("Select Option : ");
                                i = s.nextInt();
                                s.nextLine();
                            }while(i>0 && i<n);
                           
                           str = "update tbl_options set votes = votes + 1 where option_value = "+options[i-1]+" and poll_id = "+poll_id; 
                           out.writeUTF("vote");
                           out.writeUTF(str);
                           
                           System.out.println("Vote succesfully done");
                           System.in.read();
                        //    out.writeInt(poll_id);
                        //    viewPoll(in);
                        //    ds = new DatagramSocket();
                        //    sentToPollServer(poll_id, "join",ds);
                        //    TX a = new TX(ds);
                        //    a.start();
                            
                        //    do{
                        //     System.out.println("Enter 0 to quit");
                        //     i = s.nextInt();
                        //    }while(i!=0);
                           
                        }
                        else{
                            System.out.println("Poll not found");
                        }
                        break;
                    case 3:
                        System.out.println("Enter Poll Id"); 
                        poll_id = s.nextInt();
                        s.nextLine();
                        str = "select * from tbl_poll where poll_id='"+poll_id+"'";
                        out.writeUTF("select-poll");
                        out.writeUTF(str);
                        if(in.readBoolean()){
                            q = in.readUTF();
                            n = in.readInt();
                            options = new String[n];
                            int votes[] = new int[n];
                            for(i=0;i<n;i++){
                                options[i] = in.readUTF();
                            }
                            out.writeInt(1);
                            System.out.println("here");
                            for(i=0;i<n;i++){
                                votes[i] = in.readInt();
                            }
                           clearConsole();
                           System.out.println("Poll found"+"\n\n");
                           System.out.println(q+"\n\n");
                           System.out.println("Option\tVotes");
                           for(i=0;i<n;i++){
                                System.out.println(options[i]+"\t"+votes[i]);
                            }
                            System.in.read();
                        }
                        else{
                            System.out.println("Poll not found");
                        }
                        break;

                }
            }
        } catch (Exception e) {
            System.out.println("Error"+e);
        }
    }

    public static void viewPoll(DataInputStream in){
        try {
            in.readUTF();

        } catch (Exception e) {
            System.out.println(e);
        }
       
    }
    public static void sentToPollServer(int poll_id, String msg, DatagramSocket ds){
        InetAddress shost = null;
        DatagramPacket dp =null;
        try{
            
            byte[] m = msg.getBytes();
            shost = InetAddress.getByName("localhost");
            dp = new DatagramPacket(m,m.length,shost,4321);
            ds.send(dp);
        }
        catch(Exception e){
            System.out.println(e);
        }
        finally{
            if (ds!=null)
                ds.close();
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
                            pollInterface(sin,sout);
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
