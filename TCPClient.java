import java.io.*;
import java.net.*;

public class TCPClient {
    public static void main(String[] args) throws IOException {

        BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
        String dns = null;
        Socket socket = null;
        PrintWriter sockOut = null;
        BufferedReader sockIn = null;

        //Prompt for DNS
        while (true) {
            System.out.println("Enter DNS:");
            dns = sysIn.readLine();
            if (dns.length() >= 1) {
                System.out.println("\nYou entered\s" + dns + "\n");
                break;
            }
            System.out.println("Invalid Input!\n");
        }

        //Establish connection
        long startTime = System.currentTimeMillis();
        try {
            socket = new Socket(dns, 5090);
            sockOut = new PrintWriter(socket.getOutputStream(), true);
            sockIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Unknown Host:\s" + dns);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("I/O Error:\s" + dns);
            System.exit(1);
        }
        long endTime = System.currentTimeMillis();
        long connectTime = endTime - startTime;
        System.out.println("RTT(connection):\s" + connectTime + "\sms");
        String host = "Host:\s" + dns + "\r\n";

        String userIn,method,fileName,version,agent,request,response;
        while (true) {

            //Collect info
            System.out.print("\nEnter HTTP Request Method:\s");
            method = sysIn.readLine().toUpperCase() + "\s";
            System.out.print("\nEnter .htm file name:\s");
            fileName = "/" + sysIn.readLine() + ".htm";
            System.out.print("\nEnter HTTP version (1.1 or 2):\s");
            version = "\sHTTP/" + sysIn.readLine() + "\r\n";
            System.out.print("\nEnter User-Agent:\s");
            agent = "User-Agent:\s" + sysIn.readLine() + "\r\n";

            //Compose HTTP Request
            request = method + fileName + version +
                host + agent + "\r\n\r\n";


            //Send HTTP Request
            long requestSent = System.currentTimeMillis();
            sockOut.print(request);
            sockOut.flush();

            //Receive HTTP Response
            response = sockIn.readLine() + "\r\n" + sockIn.readLine() +
                    "\r\n" + sockIn.readLine();
            System.out.println("\n" + response);

            //Parse fileName
            StringBuilder sb = new StringBuilder(fileName);
            fileName = (sb.delete(0,1)).toString();

            //Write requested file from server to client directory
            BufferedWriter htm = new BufferedWriter(new FileWriter(fileName,false));
            char[] buffer = new char[4096];
            int chars = sockIn.read(buffer,0,buffer.length);
            htm.write(buffer,0,chars);
            htm.flush();
            htm.close();
            long responseRec = System.currentTimeMillis();
            long responseTime = responseRec - requestSent;
            System.out.println("\nRTT(response):\s" + responseTime + "\sms");

            //Prompt user for another request
            System.out.print("\nSend another request? (Y/N): ");
            if ((userIn = sysIn.readLine()).equalsIgnoreCase("N")) {
                sockOut.close();
                sockIn.close();
                sysIn.close();
                socket.close();
                break;
            }
        }

    }
}
