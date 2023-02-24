import java.io.*;
import java.net.*;

public class SMTPServer {
    public static void main(String[] args) throws IOException {
        // Change to personal port, check client program too
        int port = 5090; // 5160 5090

        ServerSocket sSock = null;
        boolean listening = true;

        try {
            sSock = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.exit(-1);
        }

        while (listening){
            new SMTPThread(sSock.accept()).start();
        }

        sSock.close();
    }
}
