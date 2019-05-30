package com.miancky.hvislar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.miancky.hvislar.complementary.MessageAdapter;
import com.miancky.hvislar.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.miancky.hvislar.communication.ServerCommunicator.sendRequest;

public class ChatActivity extends ResponsiveActivity {

    boolean sending;
    private List<String> msgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        setTitle(intent.getStringExtra("friendName"));
        msgs= new ArrayList<>();
        }

    public void sendMessage(View view){
        sending = true;
        Map<String, String> params = new HashMap<>();
        EditText editText = findViewById(R.id.sendEditText4);
        Intent intent = getIntent();
        params.put("email", intent.getStringExtra("email"));
        params.put("password", intent.getStringExtra("password"));
        params.put("message", editText.getText().toString());
        params.put("reciver_name", intent.getStringExtra("friendName"));
        sendRequest(this, getString(R.string.chat_sub_url), params);
    }

    private void establishServerConnection(){
        sending = false;
        Map<String, String> params = new HashMap<>();
        Intent intent = getIntent();
        params.put("email", intent.getStringExtra("email"));
        params.put("password", intent.getStringExtra("password"));
        params.put("interlocutor", intent.getStringExtra("friendName"));
        params.put("number_of_messages", "50");
        sendRequest(this, getString(R.string.refresh_sub_url), params);
    }

    public void establishServerConnection(View view){
        establishServerConnection();
    }

    private void refreshMessages(List<String> msgs){
        JSONObject JSONTemp;
        List<String> messages = new ArrayList<>();
        List<String> users = new ArrayList<>();
        for (String result: msgs) {
            try {
                JSONTemp = new JSONObject(result);
                messages.add(JSONTemp.getString("content"));
                users.add(JSONTemp.getString("sender"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ListView messagesList = findViewById(R.id.messages_view);
        MessageAdapter ad = new MessageAdapter(ChatActivity.this, messages, users);
        messagesList.setAdapter(ad);
    }

    @Override
    public void positiveResponseReaction(JSONObject response) {
        if(sending) establishServerConnection();
        else{
            try {
                JSONArray receivedMessages = response.getJSONArray("fetched_messages");
                for (int i = 0; i < receivedMessages.length(); i++)
                    msgs.add(receivedMessages.getString(i));
            }catch (JSONException e) {
                e.printStackTrace();
            }
            refreshMessages(msgs);
        }
    }

    @Override
    public void negativeResponseReaction(JSONObject response) {
        if(sending) Toast.makeText(ChatActivity.this, R.string.message_failure, Toast.LENGTH_LONG).show();
        else{
            Toast.makeText(ChatActivity.this, R.string.refresh_failure, Toast.LENGTH_LONG).show();
            refreshMessages(msgs);
        }
    }

    @Override
    public void errorReaction() {
        if(sending) Toast.makeText(ChatActivity.this, R.string.message_failure, Toast.LENGTH_LONG).show();
        else Toast.makeText(this.getApplicationContext(),R.string.processing_error,Toast.LENGTH_LONG).show();
    }
}
