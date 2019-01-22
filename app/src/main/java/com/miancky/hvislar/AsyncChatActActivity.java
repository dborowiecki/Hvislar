package com.miancky.hvislar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AsyncChatActActivity extends AppCompatActivity {
    WebSocketClient mWebSocketClient;
    Intent intent;
    List<String> usersIn = new LinkedList<>();
    ArrayList<String> listItems = new ArrayList<>();
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_chat_act);
        intent = getIntent();

        username = intent.getStringExtra("name");
        //TODO: HAVE TO DISCONNECT ON RETURNING FROM VIEW
        ArrayAdapter<String> ad = new ArrayAdapter<>(AsyncChatActActivity.this, android.R.layout.simple_list_item_1, listItems);


        final ListView messages = findViewById(R.id.lvMessages);
        // create the ArrayList to store the titles of nodes
        connectWebSocket(ad, messages);


        // give adapter to ListView UI element to render
        messages.setAdapter(ad);


        updateMessages(ad);
        messages.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            //TODO: should open conversation with friend
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                Object listItem = messages.getItemAtPosition(position);
                Toast.makeText(AsyncChatActActivity.this, listItem.toString(), Toast.LENGTH_SHORT).show();
                voteForUser(listItem.toString());


            }
        });

    }

    private void connectWebSocket(final ArrayAdapter adapter, final ListView lV) {
        URI uri;
        Map<String,String> httpHeaders = new HashMap<>();
        //TODO: Should take login and password from intent
        final String name = intent.getStringExtra("name");
        final String password = intent.getStringExtra("password");
        final String roomName = intent.getStringExtra("roomName");
        //TODO: DEFAULT BUILD FOR TEMP CHAT WITHOUT USER AUTH, REMOVE IT LATER
        if( name == null ||password ==null) {
            //AUTHENTICATION STRING BUILD AS String 'login,password'
           // httpHeaders.put( "auth", "dobryKolega,123qwe" );
            //roomName = roomName!=null? roomName : "lobby";

        }
        else
            httpHeaders.put("auth",name+","+password);


        try {
            uri = new URI("ws://"+getString(R.string.ip)+ getString(R.string.port) + "/ws/chat/"+roomName+"/");
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
              //  mWebSocketClient.send(arr.toString());
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
                              //  TextView textView = findViewById(R.id.usersTextView);
                                //TODO: CHANGE FOR SOME KIND OF OBJECT WITH POSSIBILITY OF VOTE
                                StringBuilder userList = new StringBuilder("USERS:");
                                for(String user : users) {
                                    usersIn.add(user);

                                    //TODO: remove later
                                    userList.append("\n" + user);

                                }

                                listItems.add(userList.toString());
                                //textView.setText(userList.toString());
                            }
                        });
                    }




                    if(recived.get("type").toString().equals("voting_status")){
                        JSONArray removedUsers = recived.getJSONArray("removed");

                        for(String user: users)
                            for(int j=0; j<removedUsers.length();j++)
                                if(removedUsers.getString(j).equals(user))
                                    users.remove(user);

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

                        usersIn = users;
                    }

                    if(recived.get("type").toString().equals("chat_message")) {
                        final String message = recived.get("message").toString();
                        final String sender = recived.get("sender").toString();
                        System.out.println("SENDER: "+sender);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                String rowText = "";

                                rowText=sender+": "+message;

                                listItems.add(rowText);

                            }
                        });

                     //   adapter.notifyDataSetChanged();
                    }

                    if(recived.get("type").toString().equals("warning")) {
                        final String message = recived.get("message").toString();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listItems.add(message);
                            }
                        });
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    Log.i("WebsocketMessage", e.getMessage());
                }
                finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            adapter.notifyDataSetChanged();

                        }
                    });
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
            sentObject.put("sender", username);
            mWebSocketClient.send(sentObject.toString());
            editText.setText("");
        }
        catch (Exception e){
            Log.i("SendingMessage", e.getMessage());
        }
    }
    public void voteForUser(String listString) {
        Toast.makeText(AsyncChatActActivity.this, "aaaaaaaaaaaaaaaaaaaaaaaaaaaa", Toast.LENGTH_SHORT).show();
        String votedUser = listString.split(":")[0];
        Toast.makeText(AsyncChatActActivity.this, "bbbbbbbbbbbbbbbbbbbbbbbbbbbbb", Toast.LENGTH_SHORT).show();
        System.out.print("VOTED USER: "+votedUser);
        Boolean correct = checkVote(votedUser);
        Toast.makeText(AsyncChatActActivity.this, "ccccccccccccccccccccccccccccc", Toast.LENGTH_SHORT).show();
        if(correct) {
            try {
                JSONObject sentObject = new JSONObject();
                sentObject.put("vote", votedUser);
                mWebSocketClient.send(sentObject.toString());
                Toast.makeText(AsyncChatActActivity.this, "voted for " +votedUser, Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Log.i("Voting", e.getMessage());
            }
        }
    }

    private boolean checkVote(String username){
        for (String user: usersIn) {
            Toast.makeText(AsyncChatActActivity.this, user + " " + username, Toast.LENGTH_SHORT).show();

            if (user.equals(username)) {
                Toast.makeText(AsyncChatActActivity.this, "dddddddddddddddddddddddddddddddddddddddd", Toast.LENGTH_SHORT).show();
                return true;
            }
        }

        return false;
    }
    private void updateMessages(final ArrayAdapter a){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                a.notifyDataSetChanged();

            }
        });
    }
}
