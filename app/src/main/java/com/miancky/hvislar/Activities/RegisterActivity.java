package com.miancky.hvislar.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.miancky.hvislar.R;

import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static com.miancky.hvislar.Complementary.Security.checkEmail;
import static com.miancky.hvislar.Complementary.Security.checkPassword;
import static com.miancky.hvislar.Complementary.Security.checkUserName;
import static com.miancky.hvislar.Complementary.Security.hashString;
import static com.miancky.hvislar.ServerCommunication.ServerCommunicator.sendRequest;

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

    private void registerUser() throws NoSuchAlgorithmException {
        String password = ((EditText) findViewById(R.id.tPassword)).getText().toString().trim();
        sentUsername = ((EditText) findViewById(R.id.tName)).getText().toString().trim();
        sentEmail = ((EditText) findViewById(R.id.tEmail)).getText().toString().toLowerCase().trim();
        sentPassword = hashString(password);
        Context cxt = getApplicationContext();
        if(!checkPassword(password, cxt) || !checkEmail(sentEmail, cxt) || !checkUserName(sentUsername, cxt)) return;
        Map<String,String> params = new HashMap<>();
        params.put("username", sentUsername);
        params.put("password", sentPassword);
        params.put("email", sentEmail);
        sendRequest(this, getString(R.string.register_sub_url), params);
    }

    public void register(View view) {
        try {
            registerUser();
        } catch (NoSuchAlgorithmException e) {
            errorReaction();
        }
    }

    @Override
    public void positiveResponseReaction(JSONObject response) {
        Toast.makeText(getApplicationContext(),getString(R.string.registration_completed),Toast.LENGTH_LONG).show();
        Intent intent = new Intent(RegisterActivity.this, SetDescriptionActivity.class);
        intent.putExtra(getString(R.string.username_field), sentUsername);
        intent.putExtra(getString(R.string.email_field), sentEmail);
        intent.putExtra(getString(R.string.password_field), sentPassword);
        startActivity(intent);
    }

    @Override
    public void negativeResponseReaction(JSONObject response) {
        Toast.makeText(getApplicationContext(),R.string.registration_failed,Toast.LENGTH_LONG).show();
    }

    @Override
    public void errorReaction() {
        Toast.makeText(this.getApplicationContext(),R.string.processing_error,Toast.LENGTH_LONG).show();
    }
}
