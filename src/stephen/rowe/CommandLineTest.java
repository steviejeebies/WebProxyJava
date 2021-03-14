package stephen.rowe;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandLineTest {

    @Test
    void isSiteBlocked() {
        Assert.assertEquals(CommandLine.getRegexString(), "()");
        CommandLine.addBlockedSite("[^y]*youtube\\.com");
        CommandLine.addBlockedSite("facebook\\.com");
        Assert.assertEquals(CommandLine.getRegexString(), "([^y]*youtube\\.com|facebook\\.com)");

        Assert.assertFalse(CommandLine.isSiteBlocked("google.com"));
        Assert.assertTrue(CommandLine.isSiteBlocked("youtube.com"));
        Assert.assertTrue(CommandLine.isSiteBlocked("http://www.youtube.com"));
        Assert.assertTrue(CommandLine.isSiteBlocked("facebook.com"));
        Assert.assertFalse(CommandLine.isSiteBlocked("myspace.com"));
    }

    @Test
    void addBlockedSite() {
        Assert.assertEquals(CommandLine.getRegexString(), "()");
        CommandLine.addBlockedSite("youtube\\.com");
        CommandLine.addBlockedSite("facebook\\.com");
        Assert.assertEquals(CommandLine.getRegexString(), "(youtube\\.com|facebook\\.com)");
    }
}