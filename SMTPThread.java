import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SMTPThread extends Thread {
    private Socket cSock;
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
            String host = "cs3700a.msudenver.edu";
            String connected = "220 " + host;
            cSockOut.println(connected);
            cSockOut.flush();
            String fromClient;

            // Receive SMTP Requests & Send Responses
            while ((fromClient = cSockIn.readLine()) != null) {

                // Receive & Verify HELO
                while (true) {
                    System.out.println(fromClient);
                    if (fromClient.contains("HELO")) {
                        String[] parse = fromClient.split("\s");
                        String domain = parse[1];
                        String heloOk = "250 " + host + " hello " + domain;
                        cSockOut.println(heloOk);
                        cSockOut.flush();
                        break;
                    } else {
                        String heloErr = "503 5.5.2 Send hello first";
                        cSockOut.println(heloErr);
                        cSockOut.flush();
                    }
                }

                // Receive & Verify MAIL FROM
                while (true) {
                    String mailFrom = cSockIn.readLine();
                    System.out.println(mailFrom);
                    if (mailFrom.contains("MAIL FROM:")) {
                        String senderOk = "250 2.1.0 Sender OK";
                        cSockOut.println(senderOk);
                        cSockOut.flush();
                        break;
                    } else {
                        String senderErr = "503 5.5.2 Need mail command";
                        cSockOut.println(senderErr);
                        cSockOut.flush();
                    }
                }

                // Receive & Verify RCPT TO
                while (true) {
                    String rcptTo = cSockIn.readLine();
                    System.out.println(rcptTo);
                    if (rcptTo.contains("RCPT TO:")) {
                        String rcptOk = "250 2.1.5 Recipient OK";
                        cSockOut.println(rcptOk);
                        cSockOut.flush();
                        break;
                    } else {
                        String rcptErr = "503 5.5.2 Need rcpt command";
                        cSockOut.println(rcptErr);
                        cSockOut.flush();
                    }
                }

                // Receive & Verify DATA
                while (true) {
                    String data = cSockIn.readLine();
                    System.out.println(data);
                    if (data.contains("DATA")) {
                        String dataOk = "354 Start mail input; end with <CRLF>.<CRLF>";
                        cSockOut.println(dataOk);
                        cSockOut.flush();
                        break;
                    } else {
                        String dataErr = "503 5.5.2 Need data command";
                        cSockOut.println(dataErr);
                        cSockOut.flush();
                    }
                }

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
