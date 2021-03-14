package stephen.rowe;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class HTTPSHandler extends Thread {
    public HTTPSHandler(Socket browserClient, HeaderHTTP header) throws IOException {
        try{
            // Open a socket to the remote server
            Socket proxyToServerSocket = new Socket(header.getUrlFromFirstLine(), header.getPortNumber());

            // Send Connection established to the client
            browserClient.getOutputStream().write(Constants.CONNECTION_ESTABLISHED.getBytes());
            browserClient.getOutputStream().flush();

            (new Thread(new WebSocket(proxyToServerSocket, browserClient))).start();
//            Server.executor.execute(clientToServerHttps);


            try {
                byte[] buffer = new byte[4096];
                int read;
                do {
                    read = proxyToServerSocket.getInputStream().read(buffer);
                    if (read > 0) {
                        browserClient.getOutputStream().write(buffer, 0, read);
                        if (proxyToServerSocket.getInputStream().available() < 1) {
                            browserClient.getOutputStream().flush();
                        }
                    }
                } while (read >= 0);
            }
            catch (SocketTimeoutException e) {
                System.out.println("Socket timeout");
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            // Close Down Resources
            if(proxyToServerSocket != null){
                proxyToServerSocket.close();
            }

            // If socket times-out
        } catch (SocketTimeoutException e) {
        }
        catch (Exception e) {
        }
    }
}
