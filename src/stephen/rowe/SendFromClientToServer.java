package stephen.rowe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class SendFromClientToServer implements Runnable{

    InputStream proxyToClientIS;
    OutputStream proxyToServerOS;
    Socket proxyToServerSocket;
    Socket browserClient;
    public SendFromClientToServer(Socket proxyToServerSocket, Socket browserClient) {
        this.proxyToServerSocket = proxyToServerSocket;
        this.browserClient = browserClient;
    }

    public void run() {

        boolean eitherSocketIsOpen = true;

        while(eitherSocketIsOpen) {
            try {
                // Read byte by byte from client and send directly to server
                byte[] buffer = new byte[4096];
                int read;
                do {
                    read = browserClient.getInputStream().read(buffer);
                    if (read > 0) {
                        proxyToServerSocket.getOutputStream().write(buffer, 0, read);
                        if (browserClient.getInputStream().available() < 1) {
                            proxyToServerSocket.getOutputStream().flush();
                        }
                    }
                } while (read >= 0);
            } catch (SocketTimeoutException ste) {
                // TODO: handle exception
            } catch (IOException e) {

                e.printStackTrace();
            }

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
            } catch (SocketTimeoutException e) {
                System.out.println("Socket timeout");
            } catch (IOException e) {
                System.out.println("Proxy to client HTTPS read timed out");
            }

            if(proxyToServerSocket.isClosed() || browserClient.isClosed()) eitherSocketIsOpen = false;
        }

//        try {
//            // Close Down Resources
//            if (proxyToServerSocket != null) {
//                try {
//                    proxyToServerSocket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (proxyToServerSocket.getInputStream() != null) {
//                proxyToServerSocket.getInputStream().close();
//            }
//            if (proxyToServerSocket.getOutputStream() != null) {
//                proxyToServerSocket.getOutputStream().close();
//            }
//            if (browserClient.getOutputStream()  != null) {
//                browserClient.getOutputStream().close();
//            }
//        } catch(IOException e) {
//            e.printStackTrace();
//        }
    }
}