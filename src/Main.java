import client.KVTaskClient;
import server.HttpTaskServer;
import server.KVServer;

import java.io.IOException;
import java.net.URI;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        new KVServer().start();
        new HttpTaskServer().start();
    }
}
