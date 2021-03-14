package stephen.rowe;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class SendFromServerToClient implements Runnable {

    Socket proxyToServerSocket;
    Socket browserClient;

    public SendFromServerToClient(Socket proxyToServerSocket, Socket browserClient) {
        this.proxyToServerSocket = proxyToServerSocket;
        this.browserClient = browserClient;
    }

    public void run() {
        // gets input from Server, relays output to Client


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
