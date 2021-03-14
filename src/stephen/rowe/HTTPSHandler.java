package stephen.rowe;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class HTTPSHandler extends Thread {
    public HTTPSHandler(Socket browserClient, HTTPHeader header) throws IOException {
        BufferedWriter proxyToClientBw;
        proxyToClientBw = new BufferedWriter(new OutputStreamWriter(browserClient.getOutputStream()) );

        try{
            // Only first line of HTTPS request has been read at this point (CONNECT *)
            // Read (and throw away) the rest of the initial data on the stream
//            for(int i=0;i<5;i++){
//                proxyToClientBr.readLine();
//            }


            // Get actual IP associated with this URL through DNS
//            InetAddress address = InetAddress.getByName(header.getUrlFromFirstLine());

            // Open a socket to the remote server
            Socket proxyToServerSocket = new Socket(header.getUrlFromFirstLine(), header.getPortNumber());
            proxyToServerSocket.setSoTimeout(5 * 1000);

            // Send Connection established to the client
            proxyToClientBw.write(Constants.CONNECTION_ESTABLISHED);
            proxyToClientBw.flush();

            // Client and Remote will both start sending data to proxy at this point
            // Proxy needs to asynchronously read data from each party and send it to the other party


            //Create a Buffered Writer betwen proxy and remote
            BufferedWriter proxyToServerBW = new BufferedWriter(new OutputStreamWriter(proxyToServerSocket.getOutputStream()));

            // Create Buffered Reader from proxy and remote
            BufferedReader proxyToServerBR = new BufferedReader(new InputStreamReader(proxyToServerSocket.getInputStream()));

            // Create a new thread to listen to client and transmit to server
            WebSocket clientToServerHttps =
                    new WebSocket(proxyToServerSocket, browserClient);

            (new Thread(clientToServerHttps)).start();
//            Server.executor.execute(clientToServerHttps);


            SendFromServerToClient serverToClientHttps =
                    new SendFromServerToClient(proxyToServerSocket, browserClient);

            Server.executor.execute(serverToClientHttps);
//
//            (new Thread(serverToClientHttps)).start();
//             gets input from Server, relays output to Client
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
            if(proxyToServerBR != null){
                proxyToServerBR.close();
            }
            if(proxyToServerBW != null){
                proxyToServerBW.close();
            }
            if(proxyToClientBw != null){
                proxyToClientBw.close();
            }

            // If socket times-out
        } catch (SocketTimeoutException e) {
            String line = "HTTP/1.0 504 Timeout Occured after 10s\n" +
                    "User-Agent: ProxyServer/1.0\n" +
                    "\r\n";
            try{
                proxyToClientBw.write(line);
                proxyToClientBw.flush();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        catch (Exception e){
            System.out.println("Error on HTTPS : " + header.getUrlFromFirstLine() );
            e.printStackTrace();
        }       // general error
    }
}
