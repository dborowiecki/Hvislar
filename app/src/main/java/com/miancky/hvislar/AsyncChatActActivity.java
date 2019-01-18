package com.miancky.hvislar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

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
        Map<String,String> httpHeaders = new HashMap<>();
        //TODO: Should take login and password from intent
        //AUTHENTICATION STRING BUILD AS String 'login,email'
        Intent intent = getIntent();
        httpHeaders.put( "auth", intent.getStringExtra("name")+","+intent.getStringExtra("password") );
        try {
            //TODO: Fetching ip from R.string.id dont work, needed fix
            uri = new URI("ws://"+R.string.ip+":8000/ws/chat/lobby/");

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        /* W httpHeader powinien być token uwierzytelniający zamiast loginu i hasła
        *
         */
        mWebSocketClient = new WebSocketClient(uri, httpHeaders) {

            @Override
            public void onOpen(ServerHandshake serverHandshake) {

                Log.i("Websocket", "Opened");
                String message = "Hello from " + Build.MANUFACTURER + " " + Build.MODEL;
                mWebSocketClient.setAttachment("pomocy");

                //TODO: Additional messages should be in sended json object
                //TODO: in final version shouldnt push this message
                JSONObject arr = new JSONObject();
                try {
                    arr.put("message", message);
                    arr.put("login", "test1");
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
                            TextView textView = findViewById(R.id.chatTextView);
                            String temp=textView.getText() + "\n" + message;
                            textView.setText(temp);
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
        EditText editText = findViewById(R.id.sendEditText);
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
