package com.miancky.hvislar.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.miancky.hvislar.Complementary.UserProfile;
import com.miancky.hvislar.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.miancky.hvislar.ServerCommunication.ServerCommunicator.sendRequest;

public class LoginActivity extends ResponsiveActivity{

    private String sentPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void goToRegisterScreen(View view){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void loginUser() {
        Map<String, String> params = new HashMap<>();
        sentPassword = ((EditText) findViewById(R.id.tPassword)).getText().toString().trim();
        params.put(getString(R.string.password_field), sentPassword);
        params.put(getString(R.string.email_field), ((EditText) findViewById(R.id.tEmail)).getText().toString().toLowerCase().trim());
        sendRequest(this, getString(R.string.login_sub_url), params);
    }

    public void login(View view) {
            loginUser();
    }

    @Override
    public void positiveResponseReaction(JSONObject response) {
        try {
            Intent intent = new Intent( this.getApplicationContext(), UserProfile.class);
            intent.putExtra(getString(R.string.username_field), response.getString(getString(R.string.username_field)));
            intent.putExtra(getString(R.string.email_field), response.getString(getString(R.string.email_field)));
            intent.putExtra(getString(R.string.password_field), sentPassword);
            startActivity(intent);
        } catch (JSONException e) {
            errorReaction();
        }
    }

    @Override
    public void negativeResponseReaction(JSONObject response) {
        Toast.makeText(this.getApplicationContext(),R.string.incorrect_login_data,Toast.LENGTH_LONG).show();
    }

    @Override
    public void errorReaction() {
        Toast.makeText(this.getApplicationContext(),R.string.processing_error,Toast.LENGTH_LONG).show();
    }
}
