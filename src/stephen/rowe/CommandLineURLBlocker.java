package stephen.rowe;

import java.util.Scanner;
import java.util.regex.*;

// This class has two purposes:
// 1. To take in input from the user on the command line (in IDE,
//    not in the Swing window).
// 2. Keeps track of the blocked websites, and provides the
//    isSiteBlocked() function that can be used anywhere in the
//    program

// The user will input the sites they want to block
// into the command line, with regular expressions.
// So we need to retain a list of regex expressions,
// to test any given HTTP url request against.
// I will implement this with a string, which will
// look something like this: "(mywebsite\\.com|yousomething\\.com|...)"
// This will save storing every single string and iterating
// for every single pattern, we can just match once for the whole
// list of regexes.

public class CommandLineURLBlocker extends Thread {
    static private String blockedPattern = "(";
    static private boolean blockedPatternHasBeenChanged = true;
    static private Pattern regex;
    Scanner scanner;

    static public boolean isSiteBlocked (String URL) {
        if (blockedPattern.length() <= 1) return false;

        // if our concatenated regular expression has been
        // added to since the last time we called this function,
        // then we need to re-compile the regex pattern.
        if (blockedPatternHasBeenChanged) {
            regex = Pattern.compile(blockedPattern + ")");
            blockedPatternHasBeenChanged = false;
        }
        return regex.matcher(URL).matches();
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

    public CommandLineURLBlocker() {

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
                    CommandLineURLBlocker.addBlockedSite(newRegex);
                    System.out.println("ACCEPTED NEW BLOCKING REGEX: \"" + newRegex + "\"");

                } catch (Exception e) {
                    // If the string is not a valid regular expression,
                    // all we can do to notify them is post a message
                    // to the Window, (as System.out is currently set to that
                    System.out.println("THE REGEX YOU INPUTTED, \"" + newRegex + "\" IS INVALID!");
                }
            }
            try {
                Thread.sleep(2 * 1000);     // check for new input every 2 seconds
            } catch (InterruptedException e) {}
        }
    }




}
