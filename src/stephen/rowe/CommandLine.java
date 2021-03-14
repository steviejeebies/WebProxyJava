package stephen.rowe;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.*;

public class CommandLine extends Thread {

    static private String blockedPattern = "(";
    static private boolean blockedPatternHasBeenChanged = true;
    static private Pattern regex;
    Scanner scanner;

    static public boolean isSiteBlocked (String URL) {
        if (blockedPattern.length() <= 1) return false;

        if (blockedPatternHasBeenChanged) {
            regex = Pattern.compile(blockedPattern + ")");
            blockedPatternHasBeenChanged = false;
        }

        Matcher testAgainstURL = regex.matcher(URL);

        return testAgainstURL.matches();
    }

    static public void addBlockedSite(String regularExpression) {
        if(blockedPattern.length() <= 1)
            blockedPattern += regularExpression;
        else
            blockedPattern += "|" + regularExpression;

        blockedPatternHasBeenChanged = true;
    }

    static public String getRegexString() {
        return blockedPattern + ")";
    }

    public CommandLine() {
        // The user will input the sites they want to block
        // into the command line, with regular expressions.
        // So we need to retain a list of regex expressions,
        // to test a HTTP url request against
        // I will implement this with a string, which will
        // look something like this: "(mywebsite\\.com|yousomething\\.com|...)"
        // This will save storing every single string and iterating
        // for every single pattern
        scanner = new Scanner(System.in);
    }
    public void run() {
        while(true) {
            String newRegex;
            if (scanner.hasNextLine()) {
                newRegex = scanner.nextLine();
                try {
                    // Test to see if it is a valid regex expression
                    Pattern test = Pattern.compile(newRegex);
                    CommandLine.addBlockedSite(newRegex);
                    System.out.println("ACCEPTED NEW BLOCKING REGEX: \"" + newRegex + "\"");

                } catch (Exception e) {
                    // If the string is not a valid regular expression,
                    // all we can do to notify them is post a message
                    // to the Window, (as System.out is currently set to that
                    System.out.println("THE REGEX YOU INPUTTED, \"" + newRegex + "\" IS INVALID!");
                }
            }
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {}
        }
    }




}
