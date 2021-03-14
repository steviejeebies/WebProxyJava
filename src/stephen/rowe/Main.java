// RESOURCES:
// https://www.baeldung.com/a-guide-to-java-sockets
// https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API/Writing_a_WebSocket_server_in_Java
// https://www.baeldung.com/java-http-request

package stephen.rowe;

import java.net.*;
import java.net.http.*;
import java.io.*;
import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.function.IntPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

//
// You can ignore this class, I just used it for experimenting
//

public class Main {
    public int parseAndMul10(String x) {
        return Integer.parseInt(x) * 10;
    }

    Callable<String> callable(String result, long sleepSeconds) {
        return () -> {
            TimeUnit.SECONDS.sleep(sleepSeconds);
            return result;
        };
    }

    public static void main(String [] args) {
        Pattern hostAddress = Pattern.compile("^Host: ([^\\r\\n]*)\\r\\n", Pattern.MULTILINE);
        Matcher match = hostAddress.matcher("GET http://neverssl.com/ HTTP/1.1\r\nHost: neverssl.com\r\nUser-Agent");
        match.find();
        System.out.println("FOUND:"+match.group(1)+"0");


        // .mapToInt returns an IntStream consisting of the results of applying
        // the given function to the elements of this stream


//        List<String> myList =
//                Arrays.asList("a1", "a2", "b1", "c2", "c1");
//        myList
//                .stream()
//                .filter(s -> s.startsWith("c"))
//                .map(String::toUpperCase)
//                .sorted();
//
//        System.out.println(myList.toString());
    }
}
