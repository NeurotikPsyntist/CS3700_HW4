import java.io.IOException;
import java.net.ServerSocket;

public class SMTPServer {
    public static void main(String[] args) throws IOException {
        ServerSocket sSock = null;
        boolean listening = true;

        try {
            sSock = new ServerSocket(5090);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 5090.");
            System.exit(-1);
        }

        while (listening){
            new SMTPThread(sSock.accept()).run();
        }

        sSock.close();
    }
}
