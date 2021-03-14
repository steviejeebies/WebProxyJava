package stephen.rowe;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    @Test
    public void givenGreetingClient_whenServerRespondsWhenStarted_thenCorrect() {
        Client client = new Client();
        String response = "NOTHING RECIEVED";
        try {
            client.startConnection("127.0.0.1", 6666);
            response = client.sendMessage("hello server");
        } catch(IOException e) {
            e.printStackTrace();
        }
        assertEquals("hello client", response);
    }
}