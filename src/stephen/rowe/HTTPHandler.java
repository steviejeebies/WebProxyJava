package stephen.rowe;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class HTTPHandler extends Thread {

    public HTTPHandler(Socket browserClient, String serverAddress, String fullHTTPRequest) throws IOException {
        InetAddress addr = InetAddress.getByName(serverAddress);
        Socket remoteServerSocket = new Socket(addr, 80);
        browserClient.getOutputStream().write(Constants.CONNECTION_ESTABLISHED.getBytes());
        browserClient.getOutputStream().flush();

        remoteServerSocket.getOutputStream().write(fullHTTPRequest.getBytes());
        remoteServerSocket.getOutputStream().flush();

        System.out.println(fullHTTPRequest);

//        System.out.println("NOT FROM SERVER BELOW");
//        System.out.println(fullHTTPRequest);
//        System.out.println("NOT FROM SERVER ABOVE");

        byte[] buffer = new byte[4096];
        int read;
        do {
            read = remoteServerSocket.getInputStream().read(buffer);

            if (read > 0) {
                System.out.write(buffer, 0, read);
                browserClient.getOutputStream().write(buffer, 0, read);
                if (remoteServerSocket.getInputStream().available() < 1) {
                    System.out.flush();
                    browserClient.getOutputStream().flush();
                }
            }
        } while (read >= 0);

    }
}
