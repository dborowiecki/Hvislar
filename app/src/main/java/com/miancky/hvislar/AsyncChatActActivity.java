package com.miancky.hvislar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

public class AsyncChatActActivity extends AppCompatActivity {
    WebSocketClient mWebSocketClient;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_chat_act);
        intent = getIntent();
        TextView chatWindow = findViewById(R.id.chatTextView);

        chatWindow.setText("Wiadomosc+\n");

        //TODO: HAVE TO DISCONNECT ON RETURNING FROM VIEW
        connectWebSocket();
    }

    private void connectWebSocket() {
        URI uri;
        Map<String,String> httpHeaders = new HashMap<>();
        //TODO: Should take login and password from intent
        String name = intent.getStringExtra("name");
        String password = intent.getStringExtra("password");
        String roomName = intent.getStringExtra("roomName");
        //TODO: DEFAULT BUILD FOR TEMP CHAT WITHOUT USER AUTH, REMOVE IT LATER
        if( name == null ||password ==null) {
            //AUTHENTICATION STRING BUILD AS String 'login,password'
           // httpHeaders.put( "auth", "dobryKolega,123qwe" );
            //roomName = roomName!=null? roomName : "lobby";

        }
        else
            httpHeaders.put("auth",name+","+password);


        try {
            uri = new URI("ws://"+getString(R.string.ip)+":8000/ws/chat/"+roomName+"/");
            Toast.makeText(AsyncChatActActivity.this,uri.toString(),Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        /* W httpHeader powinien być token uwierzytelniający zamiast loginu i hasła
        *
         */
        mWebSocketClient = new WebSocketClient(uri, httpHeaders) {
            LinkedList<String> users;

            @Override
            public void onOpen(ServerHandshake serverHandshake) {

                Log.i("Websocket", "Opened");
                String message = "Hello from " + Build.MANUFACTURER + " " + Build.MODEL;
                mWebSocketClient.setAttachment("pomocy");

                //TODO: Additional messages should be in sent json object
                //TODO: in final version shouldn't push this message
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
                    if(recived.get("type").toString().equals("countdown")){
                         //TODO: build timer that will count to battle royal start
                         //buildTimer()
                        final String  countdown = recived.get("time").toString();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView textView = findViewById(R.id.timerTextView);
                                String temp= countdown + "/ " +"10";
                                textView.setText(temp);

                            }
                        });
                    }

                    if(recived.get("type").toString().equals("usernames")){
                        JSONArray temp = recived.getJSONArray("usernames");
                        users = new LinkedList<>();
                        for(int i=0;i<temp.length();i++){
                            users.add(temp.getString(i));
                        }
                        final JSONArray startUsers = temp;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView textView = findViewById(R.id.usersTextView);
                                //TODO: CHANGE FOR SOME KIND OF OBJECT WITH POSSIBILITY OF VOTE
                                StringBuilder userList = new StringBuilder("USERS:");
                                for(String user : users)
                                    userList.append("\n"+user);

                                textView.setText(userList.toString());
                            }
                        });
                    }

                    if(recived.get("type").toString().equals("voting_status")){
                        JSONArray removedUsers = recived.getJSONArray("removed");

                        for(String user: users)
                            for(int j=0; j<removedUsers.length();j++)
                                if(removedUsers.getString(j).equals(user))
                                    users.remove(user);


                        final List updatedUsers = users;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView textView = findViewById(R.id.usersTextView);

                                StringBuilder userList = new StringBuilder("USERS:");
                                for(String user : users)
                                    userList.append("\n"+user);

                                textView.setText(userList);
                            }
                        });
                    }

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
                    e.printStackTrace();
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
            JSONObject sentObject = new JSONObject();
            sentObject.put("message", editText.getText());
            mWebSocketClient.send(sentObject.toString());
            editText.setText("");
        }
        catch (Exception e){
            Log.i("SendingMessage", e.getMessage());
        }
    }
}
