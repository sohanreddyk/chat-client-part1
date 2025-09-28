package com.chatflow.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.*;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Chat Client Part 1
 * - Spawns worker threads
 * - Each sends messages to server
 * - Records latency and success/failure
 * - Writes results to results.csv
 */
public class ChatClient {

    private static final int NUM_WORKERS = 32;     // number of threads
    private static final int NUM_MESSAGES = 50000; // total messages

    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(NUM_WORKERS);
        CountDownLatch latch = new CountDownLatch(NUM_MESSAGES);

        try (PrintWriter writer = new PrintWriter(new FileWriter("results.csv"))) {
            writer.println("latency_ms,success");

            for (int i = 0; i < NUM_MESSAGES; i++) {
                pool.submit(() -> {
                    try {
                        long start = System.nanoTime();
                        TestWebSocketClient client = new TestWebSocketClient(new URI("ws://localhost:9090/chat/1"));
                        client.connectBlocking();
                        client.send("Hello from client!");
                        client.close();
                        long end = System.nanoTime();
                        long latency = TimeUnit.NANOSECONDS.toMillis(end - start);
                        writer.println(latency + ",true");
                    } catch (Exception e) {
                        writer.println("0,false");
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
        }

        pool.shutdown();
        System.out.println("🚀 Client Part 1 finished sending " + NUM_MESSAGES + " messages!");
    }

    // Inner class that represents one WebSocket client
    static class TestWebSocketClient extends WebSocketClient {
        public TestWebSocketClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshake) {}

        @Override
        public void onMessage(String message) {}

        @Override
        public void onClose(int code, String reason, boolean remote) {}

        @Override
        public void onError(Exception ex) {}
    }
}
