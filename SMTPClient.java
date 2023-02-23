import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SMTPClient {
    public static void main(String[] args) throws IOException {
        // Change to personal port, check server program too
        int port = 5090; // 5160 5090

        BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
        String host;
        Socket sock = null;
        PrintWriter sockOut = null;
        BufferedReader sockIn = null;

        // Prompt for Host
        while (true) {
            System.out.println("Enter Host Name of SMTP Server:");
            host = userIn.readLine();
            if (host.length() >= 1) {
                System.out.println("\nYou entered " + host + "\n");
                break;
            }
            System.out.println("Invalid Input!\n");
        }

        // Establish connection
        try {
            long attConnect = System.currentTimeMillis();
            sock = new Socket(host, port);
            sockOut = new PrintWriter(sock.getOutputStream(), true);
            sockIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            long connectRTT = attConnect - System.currentTimeMillis();
            System.out.println("RTT(connection): " + connectRTT + " ms");
        } catch (UnknownHostException e) {
            System.err.println("Unknown Host: " + host);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("I/O Error: " + host);
            System.exit(1);
        }

        String connected = sockIn.readLine();
        System.out.println(connected);

        while (true) {

            // Collect info
            System.out.print("\nEnter Sender's Email: ");
            String sender = userIn.readLine().toUpperCase();
            String[] parse = sender.split("@");
            String domain = parse[1];
            System.out.print("\nEnter Receiver's Email: ");
            String receiver = userIn.readLine();
            System.out.print("\nEnter Email Subject: ");
            String subject = userIn.readLine();
            System.out.print("\nEnter Content: ");
            String content = userIn.readLine();

            // Compose & Send HELO
            String helo = "HELO " + domain;
            long heloSent = System.currentTimeMillis();
            sockOut.println(helo);
            sockOut.flush();

            // Receive Server "Hello"
            String servHello = sockIn.readLine();
            long heloRTT = heloSent - System.currentTimeMillis();
            System.out.println(servHello);
            System.out.println("RTT (HELO): " + heloRTT + " ms");

            // Compose & Send MAIL FROM
            String from = "MAIL FROM: " + sender;
            long fromSent = System.currentTimeMillis();
            sockOut.println(from);
            sockOut.flush();

            // Receive Server "Sender OK"
            String servFrom = sockIn.readLine();
            long fromRTT = fromSent - System.currentTimeMillis();
            System.out.println(servFrom);
            System.out.println("RTT (MAIL FROM): " + fromRTT + " ms");

            // Compose & Send RCPT TO
            String to = "RCPT TO: " + receiver;
            long rcptSent = System.currentTimeMillis();
            sockOut.println(to);
            sockOut.flush();

            // Receive Server "Recipient OK"
            String rcptOk = sockIn.readLine();
            long rcptRTT = rcptSent - System.currentTimeMillis();
            System.out.println(rcptOk);
            System.out.println("RTT (RCPT TO): " + rcptRTT + " ms");

            // Compose & Send DATA
            String data = "DATA";
            long dataSent = System.currentTimeMillis();
            sockOut.println(data);
            sockOut.flush();

            // Receive Server "Start mail input"
            String startMail = sockIn.readLine();
            long dataRTT = dataSent - System.currentTimeMillis();
            System.out.println(startMail);
            System.out.println("RTT (DATA): " + dataRTT + " ms");

            // Prompt to continue
            System.out.print("\nContinue? ('QUIT' to exit): ");
            if ((userIn.readLine()).equalsIgnoreCase("QUIT")) {
                sockOut.println("QUIT");
                sockOut.flush();
                sockOut.close();
                sockIn.close();
                userIn.close();
                sock.close();
                break;
            }
        }
    }
}