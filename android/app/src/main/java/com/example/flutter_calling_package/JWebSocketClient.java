package com.example.flutter_calling_package;

//import android.util.Log;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

public class JWebSocketClient extends WebSocketClient {
    public JWebSocketClient(URI serverUri) {
        super(serverUri, new Draft_6455());
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
//        Log.e("JWebSocketClient", "onOpen()");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("message");
    }




    @Override
    public void onMessage(ByteBuffer message) {
        System.out.println("byte buffer");
//        Log.e("JWebSocketClient", "onMessage()");

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
//        Log.e("JWebSocketClient", "onClose()");
    }

    @Override
    public void onError(Exception ex) {
//        Log.e("JWebSocketClient", "onError()", ex);
    }
}