import java.io.*;
import java.net.Inet4Address;
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
            String connected = "220 " + host + "\r\n";
            cSockOut.print(connected);
            cSockOut.flush();

            boolean heloLoop = true;
            boolean mailLoop = true;
            boolean rcptLoop = true;
            boolean dataLoop = true;
            boolean msgLoop = true;


            // Receive SMTP Requests & Send Responses
            while (true) {

                String fromClient = cSockIn.readLine();
                if (fromClient.equals("QUIT")) {
                    System.out.println("Client Quit");
                    String closingTime = "221 " + cSock.getLocalAddress() +
                            " closing connection\r\n";
                    cSockOut.print(closingTime);
                    cSockOut.flush();
                    break;
                }

                // Receive & Verify HELO
                while (heloLoop) {
                    String heloUser = fromClient;
                    System.out.println(heloUser);
                    if (heloUser.startsWith("HELO")) {
                        String[] parse = heloUser.split("\s");
                        String domain = parse[1];
                        String heloOk = "250 " + host + " hello " +
                                domain + "\r\n";
                        cSockOut.print(heloOk);
                        cSockOut.flush();
                        heloLoop = false;
                    } else {
                        String heloErr = "503 5.5.2 Send hello first\r\n";
                        cSockOut.print(heloErr);
                        cSockOut.flush();
                    }
                }

                // Receive & Verify MAIL FROM
                while (mailLoop) {
                    String mailFrom = cSockIn.readLine();
                    System.out.println(mailFrom);
                    if (mailFrom.startsWith("MAIL FROM:")) {
                        String senderOk = "250 2.1.0 Sender OK\r\n";
                        cSockOut.print(senderOk);
                        cSockOut.flush();
                        mailLoop = false;
                    } else {
                        String senderErr = "503 5.5.2 Need mail command\r\n";
                        cSockOut.print(senderErr);
                        cSockOut.flush();
                    }
                }

                // Receive & Verify RCPT TO
                while (rcptLoop) {
                    String rcptTo = cSockIn.readLine();
                    System.out.println(rcptTo);
                    if (rcptTo.startsWith("RCPT TO:")) {
                        String rcptOk = "250 2.1.5 Recipient OK\r\n";
                        cSockOut.print(rcptOk);
                        cSockOut.flush();
                        rcptLoop = false;
                    } else {
                        String rcptErr = "503 5.5.2 Need rcpt command\r\n";
                        cSockOut.print(rcptErr);
                        cSockOut.flush();
                    }
                }

                // Receive & Verify DATA
                while (dataLoop) {
                    String data = cSockIn.readLine();
                    System.out.println(data);
                    if (data.startsWith("DATA")) {
                        String dataOk = "354 Start mail input; end with <CRLF>.<CRLF>\r\n";
                        cSockOut.print(dataOk);
                        cSockOut.flush();
                        dataLoop = false;
                    } else {
                        String dataErr = "503 5.5.2 Need data command\r\n";
                        cSockOut.print(dataErr);
                        cSockOut.flush();
                    }
                }

                // Receive MAIL message
                while (msgLoop) {
                    String msg = cSockIn.readLine();
                    System.out.println(msg);
                    if (msg.equals(".")) {
                        String msgOk = "250 Message received and to be delivered\r\n";
                        cSockOut.print(msgOk);
                        cSockOut.flush();
                        msgLoop = false;
                    }
                }

                //Close connection if client inputs "QUIT"
                String userQuit;
                if ((userQuit = cSockIn.readLine()).equals("\r\nQUIT\r\n")) {
                    System.out.println("Client Quit");
                    String closingTime = "221 " + cSock.getLocalAddress() +
                            " closing connection\r\n";
                    cSockOut.print(closingTime);
                    cSockOut.flush();
                    break;
                } else {
                    heloLoop = true;
                    mailLoop = true;
                    rcptLoop = true;
                    dataLoop = true;
                    msgLoop = true;
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
