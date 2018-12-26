package com.miancky.hvislar;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
        try {
            //TODO: Fetching ip from R.string.id dont work, needed fix
            uri = new URI("ws://192.168.1.101:8000/ws/chat/lobby/");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                String message = "Hello from " + Build.MANUFACTURER + " " + Build.MODEL;
                //TODO: Additional messages should be in sended json object
                //TODO: in final version shouldnt push this message
                JSONObject arr = new JSONObject();
                try {
                    arr.put("message", message);
                }
                catch (Exception e){
                    Log.i("JSONObject", e.getMessage());
                }
                mWebSocketClient.send(arr.toString());
            }
            /*
             Communication is based on JSONObjects

             */
            @Override
            public void onMessage(String s) {
                try {
                    final JSONObject recived = new JSONObject(s);
                    final String message = recived.get("message").toString();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView textView = (TextView) findViewById(R.id.chatTextView);
                            textView.setText(textView.getText() + "\n" + message);
                        }
                    });
                }
                catch (Exception e){
                    Log.i("WebsocketMessage", e.getMessage());
                }
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

    public void sendMessage(View view) {
        EditText editText = (EditText)findViewById(R.id.sendEditText);
        try {
            JSONObject sendedObject = new JSONObject();
            sendedObject.put("message", editText.getText());
            mWebSocketClient.send(sendedObject.toString());
            editText.setText("");
        }
        catch (Exception e){
            Log.i("SendingMessage", e.getMessage());
        }
    }
}
