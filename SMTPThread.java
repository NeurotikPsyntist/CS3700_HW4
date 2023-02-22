import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SMTPThread extends Thread {
    private Socket cSock = null;
    public SMTPThread(Socket sock) {
        super("SMTPThread");
        cSock = sock;
    }

    // Start thread
    public void run() {

        // Initialize streams and variables
        try {
            PrintWriter cSockOut = new PrintWriter(
                    cSock.getOutputStream(), true);
            BufferedReader cSockIn = new BufferedReader(
                    new InputStreamReader(cSock.getInputStream()));
            String dns = "cs3700a.msudenver.edu";
            String connected = "220 " + dns;
            cSockOut.print(connected);
            cSockOut.flush();
            String fromClient;
            //Receive HTTP request and response until client closes
            while ((fromClient = cSockIn.readLine()) != null) {
                while (true) {
                    String hello = fromClient;
                    String[] parse = hello.split("\s");
                    String host = parse[1];
                    String helloOk = "250 " + dns + " hello " + host;
                    String helloErr = "503 5.5.2 Send hello first";
                    if (hello.contains("HELO")) {
                        cSockOut.print(helloOk);
                        cSockOut.flush();
                        break;
                    } else {
                        cSockOut.print(helloErr);
                        cSockOut.flush();
                    }
                }
                System.out.println("End hello loop");
                //Close connection if client inputs "QUIT"
                if ((cSockIn.readLine()).equalsIgnoreCase("QUIT")) {
                    break;
                }
            }
            cSockOut.close();
            cSockIn.close();
            cSock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
