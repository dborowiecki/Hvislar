package com.miancky.hvislar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.miancky.hvislar.ServerCommunicator.sendRequest;

public class LoginActivity extends ResponsiveActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void goToRegisterScreen(View view){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void goAsyncChat(View view){
        Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
        startActivity(intent);
    }

    private void loginUser(){
        Map<String, String> params = new HashMap<>();
        params.put("password", ((EditText) findViewById(R.id.tPassword)).getText().toString().trim());
        params.put("email",((EditText) findViewById(R.id.tEmail)).getText().toString().toLowerCase().trim());
        sendRequest(this, "login", params);
    }

    public void login(View view) {
        loginUser();
    }

    @Override
    public void positiveResponseReaction(JSONObject response) {
        try {
            Intent intent = new Intent( this.getApplicationContext(), UserProfile.class);
            intent.putExtra("name",response.getString("name"));
            intent.putExtra("email", response.getString("email"));
            intent.putExtra("password", ((EditText) findViewById(R.id.tPassword)).getText().toString().trim());
            startActivity(intent);
        } catch (JSONException e) {
            errorReaction();
        }
    }

    @Override
    public void negativeResponseReaction(JSONObject response) {
        Toast.makeText(this.getApplicationContext(),"Login failed. Incorrect data!",Toast.LENGTH_LONG).show();
    }

    @Override
    void errorReaction() {
        Toast.makeText(this.getApplicationContext(),"Communication with server failed.",Toast.LENGTH_LONG).show();
    }
}
