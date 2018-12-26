package com.miancky.hvislar;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class AsyncChatActActivity extends AppCompatActivity {
    WebSocketClient mWebSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_chat_act);

        TextView chatWindow = findViewById(R.id.chatTextView);

        chatWindow.setText("Wiadomosc+\n");

        connectWebSocket();
    }

    private void connectWebSocket() {
        URI uri;
        URL url;
        try {
            //TODO: change for django server chat
            //TODO: Fetching ip from R.string.id dont work, needed fix
            uri = new URI("ws://192.168.1.101:8000/ws/chat/lobby/");
           // url = new URL("ws://"+R.string.ip+":8000/chat/lobby/");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
            //TODO: fix for fitting server methods
        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                //TODO: wrap in json objct that will fit text_data_json['message']
                String message = "Hello from " + Build.MANUFACTURER + " " + Build.MODEL;
                JSONObject arr = new JSONObject();
                try {
                    arr.put("message", message);
                }
                catch (Exception e){
                    Log.i("JSONObject", e.getMessage());
                }
                mWebSocketClient.send(arr.toString());
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = (TextView)findViewById(R.id.chatTextView);
                        textView.setText(textView.getText() + "\n" + message);
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

}
