package com.miancky.hvislar.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.miancky.hvislar.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.miancky.hvislar.complementary.Security.checkEmail;
import static com.miancky.hvislar.complementary.Security.checkPassword;
import static com.miancky.hvislar.complementary.Security.checkUserName;
import static com.miancky.hvislar.communication.ServerCommunicator.sendRequest;

public class RegisterActivity extends ResponsiveActivity {

    private String sentUsername;
    private String sentPassword;
    private String sentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void goToLoginScreen(View view){
        finish();
    }

    private void registerUser() {
        sentPassword = ((EditText) findViewById(R.id.tPassword)).getText().toString().trim();
        sentUsername = ((EditText) findViewById(R.id.tName)).getText().toString().trim();
        sentEmail = ((EditText) findViewById(R.id.tEmail)).getText().toString().toLowerCase().trim();
        Context cxt = getApplicationContext();
        if(!checkPassword(sentPassword, cxt) || !checkEmail(sentEmail, cxt) || !checkUserName(sentUsername, cxt)) return;
        Map<String,String> params = new HashMap<>();
        params.put(getString(R.string.username_reg_field), sentUsername);
        params.put(getString(R.string.password_field), sentPassword);
        params.put(getString(R.string.email_field), sentEmail);
        sendRequest(this, getString(R.string.register_sub_url), params);
    }

    public void register(View view) {
            registerUser();
    }

    @Override
    public void positiveResponseReaction(JSONObject response) {
        Toast.makeText(getApplicationContext(),getString(R.string.registration_completed),Toast.LENGTH_LONG).show();
        Intent intent = new Intent(RegisterActivity.this, SetDescriptionActivity.class);
        intent.putExtra(getString(R.string.username_field), sentUsername);
        intent.putExtra(getString(R.string.email_field), sentEmail);
        intent.putExtra(getString(R.string.password_field), sentPassword);
        startActivity(intent);
        finish();
    }

    @Override
    public void negativeResponseReaction(JSONObject response) {
        try {
            Toast.makeText(getApplicationContext(),response.getString("error"),Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            errorReaction();
        }
    }

    @Override
    public void errorReaction() {
        Toast.makeText(this.getApplicationContext(),R.string.processing_error,Toast.LENGTH_LONG).show();
    }
}
