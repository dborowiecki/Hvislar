package com.miancky.hvislar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.miancky.hvislar.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.miancky.hvislar.communication.ServerCommunicator.sendRequest;

public class UserProfile extends ResponsiveActivity {

    private String description="Default description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        final TextView username = findViewById(R.id.tUsername);
        final TextView welcomeMessage = findViewById(R.id.tHelloMessage);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");

        welcomeMessage.setText(description);
        getUserDescription(name);
        username.setText(name);
    }

    public void goAsyncChat(View view){
        getRoomName();
    }

    private void getRoomName() {
        Intent intent = getIntent();
        String password = intent.getStringExtra("password");
        String email = intent.getStringExtra("email");
        Map<String, String> params = new HashMap<>();
        params.put("password", password);
        params.put("email", email);
        sendRequest(this, getString(R.string.join_massive_conversation_sub_url), params);
    }

    public void goToFriendListScreen(View view){
        Intent intent = new Intent(UserProfile.this, ListOfFriendsActivity.class);
        intent.putExtra("name", getIntent().getStringExtra("name"));
        intent.putExtra("email", getIntent().getStringExtra("email"));
        intent.putExtra("password", getIntent().getStringExtra("password"));
        startActivity(intent);
    }

    public void goToTitleScreen(View view){
        Intent intent = new Intent(UserProfile.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void setUserDescription(String description){
        final TextView welcomeMessage = findViewById(R.id.tHelloMessage);
        welcomeMessage.setText(description);
        this.description = description;
    }

    //TODO: move to another class
    public void getUserDescription(String name) {
        final String GET_DESCRIPTION_URL = "http://" + getString(R.string.ip) +  getString(R.string.port) + "/getDescription/";
        final String username = name;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_DESCRIPTION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject JSONResponse = new JSONObject(response);
                            if (!JSONResponse.getBoolean("success"))
                                Toast.makeText(UserProfile.this, "Getting user data failed", Toast.LENGTH_LONG).show();
                            else
                                setUserDescription(JSONResponse.getString("description"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserProfile.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    @Override
    public void positiveResponseReaction(JSONObject response) {
        try {
        Intent intent = new Intent(this, AsyncChatActActivity.class);
        intent.putExtra("name", getIntent().getStringExtra("name"));
        intent.putExtra("password", getIntent().getStringExtra("password"));
        intent.putExtra("email", getIntent().getStringExtra("email"));
        intent.putExtra("roomName", response.getString("room_name"));
        startActivity(intent);
        } catch (JSONException e) {
           errorReaction();
        }
    }

    @Override
    public void negativeResponseReaction(JSONObject response) {
        Toast.makeText(this, "Fail finding room", Toast.LENGTH_LONG).show();
    }

    @Override
    public void errorReaction() {
        Toast.makeText(this.getApplicationContext(),R.string.processing_error,Toast.LENGTH_LONG).show();
    }
}
