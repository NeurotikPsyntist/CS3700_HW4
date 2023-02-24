import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.Date;

public class TCPServerThread extends Thread {
    private Socket cSock = null;
    public TCPServerThread(Socket socket) {
        super("TCPServerThread");
        cSock = socket;
    }

    //Start thread
    public void run() {

        //Intialize streams and variables
        try {
            PrintWriter cSockOut = new PrintWriter(
                cSock.getOutputStream(), true);
            BufferedReader cSockIn = new BufferedReader(
                new InputStreamReader(cSock.getInputStream()));
            String fromClient,request,fileName,status,date,toClient;
            String server = "Server:\swww.msudenver.edu\r\n";

            //Receive HTTP request and response until client closes
            while ((fromClient = cSockIn.readLine()) != null) {
                request = fromClient + "\r\n" + cSockIn.readLine() + "\r\n" +
                        cSockIn.readLine() + "\r\n" + cSockIn.readLine();
                System.out.println(request);

                //Parse HTTP request for method and filename, set status
                String[] header = request.split("\r\n");
                String[] firstLine = header[0].split("\s");
                fileName = firstLine[1];
                StringBuilder sb = new StringBuilder(fileName);
                fileName = (sb.delete(0,1)).toString();
                Path path = Paths.get(fileName);
                if (firstLine[0].contains("GET") && Files.exists(path)) {
                    status = "200:\sOK\r\n";
                } else if (header[0].contains("GET") && !Files.exists(path)) {
                    status = "404:\sFile\sNot\sFound\r\n";
                } else {
                    status = "400:\sBad\sRequest\r\n";
                }
                
                //Compose HTTP response, send to client
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date currentDate = new Date();
                date = dateFormat.format(currentDate) + "\r\n";
                toClient = status + date + server + "\r\n\r\n";
                cSockOut.print(toClient);
                cSockOut.flush();

                //Send file to client if status = 200
                if (status.equals("200:\sOK\r\n")) {
                    BufferedReader fileIn = new BufferedReader(new FileReader(fileName));
                    char[] buffer = new char[4096];
                    int chars = fileIn.read(buffer,0,buffer.length);
                    cSockOut.write(buffer,0,chars);
                    String end = "\r\n\r\n\r\n\r\n";
                    char[] endLines = end.toCharArray();
                    cSockOut.write(endLines,0, endLines.length);
                    cSockOut.flush();
                    fileIn.close();
                }

                //Close connection if client inputs "N"
                if ((cSockIn.readLine()).equalsIgnoreCase("N")) {
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
