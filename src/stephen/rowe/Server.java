package stephen.rowe;

import javax.swing.*;
import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// This class serves as the entry point of the application.
public class Server extends Thread {

    // Static ExecutorService allows us to limit the number of
    // currently running threads, add tasks to the list, and
    // add them from any part of this application.
    static ExecutorService executor = Executors.newFixedThreadPool(15);

    private ServerSocket serverSocket;

    public void start(int port) throws IOException {
        System.out.println("INPUT SITES YOU WANT TO BLOCK AS VALID REGULAR EXPRESSIONS:");
        System.out.println("MUST BE FORMATTED AS A JAVA STRING (I.E. \"YOUTUBE\\\\.COM\")");
        serverSocket = new ServerSocket(port);

        // For our command line functionality of blocking sites dynamically
        new Thread(new CommandLineURLBlocker()).start();

        // For our output terminal
        SwingUtilities.invokeLater(() ->
                new OutputTerminal().setVisible(true)
        );

        // Server constantly accepts clients at its port.
        while (true)
            new ServerThread(serverSocket.accept()).start();

        // TODO inaccessible at the moment, find somewhere to put this
        // serverSocket.close();
    }

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.start(6666);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}

