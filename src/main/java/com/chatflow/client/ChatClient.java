package com.chatflow.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class ChatClient extends WebSocketClient {

    public ChatClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("✅ Connected to server");
        // Send a test message immediately when connected
        send("{\"userId\":1,\"username\":\"user1\",\"message\":\"Hello from client!\",\"timestamp\":\"2025-09-28T02:00:00Z\",\"messageType\":\"TEXT\"}");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("📩 Server echoed: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("❌ Connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("⚠️ Error: " + ex.getMessage());
    }

    public static void main(String[] args) throws Exception {
        ChatClient client = new ChatClient(new URI("ws://localhost:9090/chat/1"));
        client.connectBlocking(); // waits until connection succeeds
    }
}
