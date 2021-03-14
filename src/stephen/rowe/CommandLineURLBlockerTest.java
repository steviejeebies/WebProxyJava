package stephen.rowe;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

class CommandLineURLBlockerTest {

    @Test
    void isSiteBlocked() {
        Assert.assertEquals(CommandLineURLBlocker.getRegexString(), "()");
        CommandLineURLBlocker.addBlockedSite("[^y]*youtube\\.com");
        CommandLineURLBlocker.addBlockedSite("facebook\\.com");
        Assert.assertEquals(CommandLineURLBlocker.getRegexString(), "([^y]*youtube\\.com|facebook\\.com)");

        Assert.assertFalse(CommandLineURLBlocker.isSiteBlocked("google.com"));
        Assert.assertTrue(CommandLineURLBlocker.isSiteBlocked("youtube.com"));
        Assert.assertTrue(CommandLineURLBlocker.isSiteBlocked("http://www.youtube.com"));
        Assert.assertTrue(CommandLineURLBlocker.isSiteBlocked("facebook.com"));
        Assert.assertFalse(CommandLineURLBlocker.isSiteBlocked("myspace.com"));
    }

    @Test
    void addBlockedSite() {
        Assert.assertEquals(CommandLineURLBlocker.getRegexString(), "()");
        CommandLineURLBlocker.addBlockedSite("youtube\\.com");
        CommandLineURLBlocker.addBlockedSite("facebook\\.com");
        Assert.assertEquals(CommandLineURLBlocker.getRegexString(), "(youtube\\.com|facebook\\.com)");
    }
}