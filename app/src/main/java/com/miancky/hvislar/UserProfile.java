package com.miancky.hvislar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity {

    private String description="Default description";

    //TODO: find a better way to get and set user's description
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        final TextView username = findViewById(R.id.tUsername);
        final TextView welcomeMessage = findViewById(R.id.tHelloMessage);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");

        welcomeMessage.setText(description);
        getUserDescription(name);
        username.setText(name);
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


}
