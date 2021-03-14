package stephen.rowe;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ServerThread extends Thread {
    // this regex is used for extracting header information from a HTTP request,
    // mainly for getting the HOST value in the HTTP header for calls other than CONNECT
    static Pattern hostAddress = Pattern.compile("^Host: ([^\\r\\n]*)\\r\\n", Pattern.MULTILINE);

    private Socket browserSocket;
//    BufferedWriter proxyToClientBw;

    public ServerThread(Socket socket) throws IOException {
        this.browserSocket = socket;
        //  proxyToClientBw = new BufferedWriter(new OutputStreamWriter(browserSocket.getOutputStream()));
    }

    public void run() {


        try {
            // First, we need to look at the input from the Browser/Client. We only need to look
            // at the first line for the moment, to see what type of request it is.
            Scanner inputFromClient =
                    new Scanner(new InputStreamReader(browserSocket.getInputStream()));

            // Just re-appending it with "\r\n" in-case we need this later
            String topLineHTTPRequest = inputFromClient.nextLine() + "\r\n";

            if (topLineHTTPRequest == null) {
                // Nothing much we can do other than return
                System.out.println("ERROR: COULDN'T READ REQUEST");
                return;
            }

            // This will extract out header info, using regular expressions
            HeaderHTTP header = new HeaderHTTP(topLineHTTPRequest);
            // This first check of isSiteBlocked() will only work on the CONNECT
            // call-type, as it is the only one that has the web-server it is
            // connecting to in the first line of the header, the rest usually
            // have the host server on its own line in the header.
            boolean siteBlocked = CommandLineURLBlocker.isSiteBlocked(header.getUrlFromFirstLine());

            switch (header.getHttpCallType()) {
                case "CONNECT":
                    if (siteBlocked) {
                        browserSocket.getOutputStream().write(Constants.CONNECTION_BLOCKED.getBytes());
                        browserSocket.getOutputStream().flush();
                        browserSocket.close();
                        return;
                    } else if (header.getPortNumber() == 443)  // if it a HTTPS request
                        new HTTPSHandler(browserSocket, header).start();
                    else if (header.getPortNumber() == 80) {
                        // For simplicity's sake, if the Browser makes a CONNECT call
                        // then I'll treat a HTTP call as a HTTPS call, because the
                        // code for CONNECT that I have running now seems to be very fragile
                        // and I don't want to mess with it. Any other type of HTTP header,
                        // it will fall down to the 'default' case of this switch block.
                        new HTTPSHandler(browserSocket, header).start();
                    }
                    break;
//              case "GET":
//              case "POST":
                default:
                    // originally intended for each HTTP call type to
                    // have its own case here, but there's no need, as we
                    // treat all of them essentially the same.

                    // easy way of splitting the head from the body:
                    inputFromClient.useDelimiter("\\r\\n\\r\\n");
                    // Caught on something simple here, if I check to see
                    // if there is a body with hasNext(), then it will block
                    // here, which is no good. So I decided to just stick with
                    // the header I've extracted already and throw that to
                    // the remote server.
                    Matcher headerMatch = hostAddress.matcher(topLineHTTPRequest);
                    if (headerMatch.find()) {
                        String serverAddress = headerMatch.group(1);
                        String fullHTTPRequest = topLineHTTPRequest + "\r\n\r\n";
                        new HTTPHandler(browserSocket, serverAddress, fullHTTPRequest).start();
                    }


            }
        } catch (Exception e) {

        }
    }
}