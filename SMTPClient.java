import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SMTPClient {
    public static void main(String[] args) throws IOException {

        BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
        String host;
        Socket sock = null;
        PrintWriter sockOut = null;
        BufferedReader sockIn = null;

        //Prompt for Host
        while (true) {
            System.out.println("Enter Host:");
            host = userIn.readLine();
            if (host.length() >= 1) {
                System.out.println("\nYou entered " + host + "\n");
                break;
            }
            System.out.println("Invalid Input!\n");
        }

        //Establish connection
        try {
            long attConnect = System.currentTimeMillis();
            sock = new Socket(host, 5090);
            sockOut = new PrintWriter(sock.getOutputStream(), true);
            sockIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            long estConnect = System.currentTimeMillis();
            System.out.println("RTT(connection): " + (estConnect - attConnect) + " ms");
        } catch (UnknownHostException e) {
            System.err.println("Unknown Host: " + host);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("I/O Error: " + host);
            System.exit(1);
        }

        while (true) {

            //Collect info
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

            //Compose & Send HELO
            String helo = "HELO " + domain;
            long helloSent = System.currentTimeMillis();
            sockOut.print(helo);
            sockOut.flush();

            // Receive Server "Hello"
            String servHello = sockIn.readLine();
            long helloRec = System.currentTimeMillis();
            System.out.println(servHello);
            System.out.println("RRT (HELO): " + (helloSent - helloRec) + " ms");

            // Compose & Send MAIL FROM
            String from = "MAIL FROM: " + sender;
            long fromSent = System.currentTimeMillis();
            sockOut.print(from);
            sockOut.flush();

            // Receive Server "Sender OK"
            String servSendOk = sockIn.readLine();
            long sendOkRec = System.currentTimeMillis();
            System.out.println(servSendOk);
            System.out.println("RTT (MAIL FROM): " + (fromSent - sendOkRec) + " ms");

            // Compose & Send RCPT TO
            String to = "RCPT TO: " + receiver;
            long rcptSent = System.currentTimeMillis();
            sockOut.print(to);
            sockOut.flush();

            // Receive Server "Recipient OK"
            String rcptOk = sockIn.readLine();
            long rcptOkRec = System.currentTimeMillis();
            System.out.println(rcptOk);
            System.out.println("RTT (RCPT TO): " + (rcptSent - rcptOkRec) + " ms");

            

            // Prompt to continue
            System.out.print("\nContinue? ('QUIT' to exit): ");
            if ((userIn.readLine()).equalsIgnoreCase("QUIT")) {
                sockOut.close();
                sockIn.close();
                userIn.close();
                sock.close();
                break;
            }
        }
    }
}