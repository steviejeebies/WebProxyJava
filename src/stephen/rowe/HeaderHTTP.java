package stephen.rowe;
import java.util.regex.*;

public class HeaderHTTP {
    static final String regexForTopLine = "^(GET|POST|CONNECT|HEAD|PUT|DELETE) (https?://)?([^ :]*):?([0-9]*)?";
    static final Pattern regexTopHeaderLine = Pattern.compile(regexForTopLine);

    String httpCallType;
    String urlFromFirstLine;
    int portNumber = 443;       // default to HTTPS if not specified

    public String getHttpCallType() {
        return httpCallType;
    }

    public String getUrlFromFirstLine() {
        return urlFromFirstLine;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public HeaderHTTP(String firstLineHeader) {
        Matcher regexMatchTopLine = regexTopHeaderLine.matcher(firstLineHeader);
        if(regexMatchTopLine.find()) {
            this.httpCallType = regexMatchTopLine.group(1);
            this.urlFromFirstLine = regexMatchTopLine.group(3);
            if (!"".equals(regexMatchTopLine.group(4))) this.portNumber = Integer.parseInt(regexMatchTopLine.group(4));
        }

//        String regexHeaders = "^([^:]*): (.*)$";
//        Matcher headerMatcher =
//                Pattern.compile(regexHeaders).matcher();
//
//        String headerSplit [][] = new String [headerParameters.length][2];
//        for(String index : headerParameters) {
//            headerSplit[i][0] =
//        }

    }
}

