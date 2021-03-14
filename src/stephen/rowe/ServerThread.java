package stephen.rowe;

import java.io.*;
import java.net.*;

class ServerThread extends Thread {
    // I'll use Regex to determine what kind of call this is/extract useful info from the header.
    // Static so that we only declare this once and it will be shared across threads.


    private Socket browserClient;
    private Thread httpsClientToServer;
    BufferedWriter proxyToClientBw;

    public ServerThread(Socket socket) throws IOException {
        this.browserClient = socket;
        this.browserClient.setSoTimeout(5 * 1000);
//        String requestString = null;
//        inputFromClient = new BufferedReader(new InputStreamReader(browserClient.getInputStream()) );
        proxyToClientBw = new BufferedWriter(new OutputStreamWriter(browserClient.getOutputStream()) );
    }

    public void run() {


        try {
            // First, we need to look at the input from the Browser/Client. We only need to look
            // at the first line for the moment, to see what type of request it is.
//            BufferedReader inputFromClient =
            BufferedReader inputFromClient =
                    new BufferedReader (new InputStreamReader(browserClient.getInputStream()) );

            // We need to append this with \r\n for when we're sending the actual
            // header on, as readLine() will omit this
            String topLineHTTPRequest = inputFromClient.readLine() + "\r\n";
            if(topLineHTTPRequest == null) {
                System.out.println("ERROR: COULDN'T READ REQUEST");
                return;
            }

            // This will extract out header info, using regular expressions
            HTTPHeader header = new HTTPHeader(topLineHTTPRequest);

            switch(header.getHttpCallType()) {
                case "CONNECT":
                    // We create a new thread for HTTPSHandler, and execute it
                    (new HTTPSHandler(browserClient, header)).start();
                    break;
                default:
                    // wouldn't make any sense that this switch block could
                    // reach this, but we'll have it as a fallback.
                    System.out.println("ERROR: HTTP call type invalid!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        // Get the Request type


//        // Check if site is blocked
//        if(Proxy.isBlocked(urlString)){
//            System.out.println("Blocked site requested : " + urlString);
//            blockedSiteRequested();
//            return;
//        }




//        else{
//            // Check if we have a cached copy
////            File file;
////            if((file = Proxy.getCachedPage(urlString)) != null){
////                System.out.println("Cached Copy found for : " + urlString + "\n");
////                sendCachedPageToClient(file);
////            } else {
//                System.out.println("HTTP GET for : " + urlString + "\n");
//            try {
//                sendNonCachedToClient(urlString);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
////            }
//        }
    }



    private void sendNonCachedToClient(String urlString) throws IOException {

        try{
//
//            // Compute a logical file name as per schema
//            // This allows the files on stored on disk to resemble that of the URL it was taken from
//            int fileExtensionIndex = urlString.lastIndexOf(".");
//            String fileExtension;
//
//            // Get the type of file
//            fileExtension = urlString.substring(fileExtensionIndex, urlString.length());
//
//            // Get the initial file name
//            String fileName = urlString.substring(0,fileExtensionIndex);
//
//
//            // Trim off http://www. as no need for it in file name
//            fileName = fileName.substring(fileName.indexOf('.')+1);
//
//            // Remove any illegal characters from file name
//            fileName = fileName.replace("/", "__");
//            fileName = fileName.replace('.','_');
//
//            // Trailing / result in index.html of that directory being fetched
//            if(fileExtension.contains("/")){
//                fileExtension = fileExtension.replace("/", "__");
//                fileExtension = fileExtension.replace('.','_');
//                fileExtension += ".html";
//            }
//
//            fileName = fileName + fileExtension;
//
//
//
//            // Attempt to create File to cache to
//            boolean caching = true;
//            File fileToCache = null;
//            BufferedWriter fileToCacheBW = null;
//
//            try{
//                // Create File to cache
//                fileToCache = new File("cached/" + fileName);
//
//                if(!fileToCache.exists()){
//                    fileToCache.createNewFile();
//                }
//
//                // Create Buffered output stream to write to cached copy of file
//                fileToCacheBW = new BufferedWriter(new FileWriter(fileToCache));
//            }
//            catch (IOException e){
//                System.out.println("Couldn't cache: " + fileName);
//                caching = false;
//                e.printStackTrace();
//            } catch (NullPointerException e) {
//                System.out.println("NPE opening file");
//            }
//
//
//
//
//
//            // Check if file is an image
//            if((fileExtension.contains(".png")) || fileExtension.contains(".jpg") ||
//                    fileExtension.contains(".jpeg") || fileExtension.contains(".gif")){
//                // Create the URL
//                URL remoteURL = new URL(urlString);
//                BufferedImage image = ImageIO.read(remoteURL);
//
//                if(image != null) {
//                    // Cache the image to disk
//                    ImageIO.write(image, fileExtension.substring(1), fileToCache);
//
//                    // Send response code to client
//                    String line = "HTTP/1.0 200 OK\n" +
//                            "Proxy-agent: ProxyServer/1.0\n" +
//                            "\r\n";
//                    proxyToClientBw.write(line);
//                    proxyToClientBw.flush();
//
//                    // Send them the image data
//                    ImageIO.write(image, fileExtension.substring(1), clientSocket.getOutputStream());
//
//                    // No image received from remote server
//                } else {
//                    System.out.println("Sending 404 to client as image wasn't received from server"
//                            + fileName);
//                    String error = "HTTP/1.0 404 NOT FOUND\n" +
//                            "Proxy-agent: ProxyServer/1.0\n" +
//                            "\r\n";
//                    proxyToClientBw.write(error);
//                    proxyToClientBw.flush();
//                    return;
//                }
//            }
//
//            // File is a text file
//            else {

                // Create the URL
                URL remoteURL = new URL(urlString);
                // Create a connection to remote server
                HttpURLConnection proxyToServerCon = (HttpURLConnection)remoteURL.openConnection();
                proxyToServerCon.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                proxyToServerCon.setRequestProperty("Content-Language", "en-US");
                proxyToServerCon.setUseCaches(false);
                proxyToServerCon.setDoOutput(true);

                // Create Buffered Reader from remote Server
                BufferedReader proxyToServerBR = new BufferedReader(new InputStreamReader(proxyToServerCon.getInputStream()));


                // Send success code to client
                String line = "HTTP/1.0 200 OK\n" +
                        "Proxy-agent: ProxyServer/1.0\n" +
                        "\r\n";
                proxyToClientBw.write(line);


                // Read from input stream between proxy and remote server
                while((line = proxyToServerBR.readLine()) != null){
                    // Send on data to client
                    proxyToClientBw.write(line);

//                    // Write to our cached copy of the file
//                    if(caching){
//                        fileToCacheBW.write(line);
//                    }
                }

                // Ensure all data is sent by this point
                proxyToClientBw.flush();

                // Close Down Resources
                if(proxyToServerBR != null){
                    proxyToServerBR.close();
                }
            } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


//            if(caching){
//                // Ensure data written and add to our cached hash maps
//                fileToCacheBW.flush();
//                Proxy.addCachedPage(urlString, fileToCache);
//            }

//            // Close down resources
//            if(fileToCacheBW != null){
//                fileToCacheBW.close();
//            }

            if(proxyToClientBw != null){
                proxyToClientBw.close();
            }
        }
    }

//    public void run() {
//        try {
//
//            // Using this delimiter to separate the HTTP header from the body
//            Scanner s = new Scanner(browserIn).useDelimiter("\\r\\n\\r\\n");
//
//            String header = "";
//            if (s.hasNext()) header = s.next();
//            String body = "";
//            if (s.hasNext()) body = s.next();
//            System.out.println(header);
//            // Regex to get the HTTP message type, the URL, and the port (for the moment, assuming this will be a
//            // CONNECT message)
//            Matcher callRegex = Pattern.compile("^(GET|POST|CONNECT|HEAD|PUT|DELETE) (https?://)?([^ :]*):?([0-9]*)?").matcher(header);
//            if(header != null) System.out.println(header);
//
////            System.out.println(Thread.currentThread().getName());
////            System.out.println("Active Threads: " + Thread.activeCount());
//
//            if(callRegex.find()) {
//                // Extract relevant info from the Regex
//                String httpHeaderType = callRegex.group(1);
//                String serverName = callRegex.group(3);
//                int port = (callRegex.group(4).equals("")) ? 80 : Integer.parseInt(callRegex.group(4));
//
//                switch (httpHeaderType) {
//                    case "CONNECT":
//                        // Just for debugging, I wanted to see how this was called:
//                        System.out.println("Socket forwardSocket = new Socket(\"" + serverName + "\", " + port + ");");
//
//                        // So we make a new socket to the Server that Firefox is trying to connect to:
//                        Socket forwardSocket = new Socket(serverName, port);
//                        forwardSocket.setSoTimeout(10 * 1000);
//
//                        // Readers and Printers to the outside Server
//                        InputStream serverIn = forwardSocket.getInputStream();
//                        PrintStream serverOut = new PrintStream(forwardSocket.getOutputStream());
//
//                        // Send the server the exact header that Firefox sent us:
//                        forwardSocket.getOutputStream().write(header.getBytes());
//                        forwardSocket.getOutputStream().flush();
//
//                        byte buffer [] = new byte [4096];
//                        serverIn.read(buffer);
//                        if(buffer.length > 0) {
//                            System.out.println("Something returned!");
//                        }
//                        else {
//                            System.out.println("Nothing returned!");
//                        }
//
//                        byte bufferClient[] = new byte [4096];
//                        clientSocket.getOutputStream().write(buffer);
//                        clientSocket.getOutputStream().flush();
//
//
//                }
//
//            } else {
//                clientSocket.close();
//            }
////
//
//        } catch (IOException e) {
//            System.out.println("ERROR");
//        }
//    }
//}