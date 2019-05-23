package com.miancky.hvislar.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.miancky.hvislar.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        setTitle(intent.getStringExtra("friendName"));
        }

    public void sendMessage(View view){
        final String SEND_MESSAGE_URL = "http://" + getString(R.string.ip) +  getString(R.string.port) + "/sendMessage/";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_MESSAGE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject JSONResponse = new JSONObject(response);
                            if (!JSONResponse.getBoolean("success"))
                                Toast.makeText(ChatActivity.this, "Cannot send message!", Toast.LENGTH_LONG).show();
                            else
                                establishServerConnection();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ChatActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                EditText editText = findViewById(R.id.sendEditText4);
                Intent intent = getIntent();
                params.put("email", intent.getStringExtra("email"));
                params.put("password", intent.getStringExtra("password"));
                params.put("message", editText.getText().toString());
                params.put("reciver_name", intent.getStringExtra("friendName"));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void establishServerConnection(View view){
        establishServerConnection();
    }

    public void establishServerConnection(){
        final String GET_MESSAGES_URL = "http://" + getString(R.string.ip) +  getString(R.string.port) + "/getMessages/";
        final List<String> msgs= new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_MESSAGES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject JSONResponse = new JSONObject(response);
                            if (!JSONResponse.getBoolean("success"))
                                Toast.makeText(ChatActivity.this, "Cannot download messages!", Toast.LENGTH_LONG).show();
                            else {
                                JSONArray receivedMessages = JSONResponse.getJSONArray("fetched_messages");
                                for(int i = 0; i < receivedMessages.length(); i++)
                                    msgs.add(receivedMessages.getString(i));
                            }
                            refreshMessages(msgs);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ChatActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                EditText editText = findViewById(R.id.sendEditText4);
                Intent intent = getIntent();
                params.put("email", intent.getStringExtra("email"));
                params.put("password", intent.getStringExtra("password"));
                params.put("interlocutor", intent.getStringExtra("friendName"));
                params.put("number_of_messages", "50");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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
}
