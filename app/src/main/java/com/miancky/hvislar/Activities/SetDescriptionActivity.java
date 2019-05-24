package com.miancky.hvislar.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.miancky.hvislar.Complementary.UserProfile;
import com.miancky.hvislar.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.miancky.hvislar.ServerCommunication.ServerCommunicator.sendRequest;

public class SetDescriptionActivity extends ResponsiveActivity {

    private String sentUsername;
    private String sentEmail;
    private String sentPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_description);
    }

    private void setDescription(){
        Intent intent = getIntent();
        sentUsername = intent.getStringExtra("name");
        sentEmail = intent.getStringExtra("email");
        sentPassword = intent.getStringExtra("password");
        String description = ((EditText) findViewById(R.id.descriptionField)).getText().toString().trim();
        if(!description.equals("")) {
            Map<String, String> params = new HashMap<>();
            params.put("email", sentEmail);
            params.put("password", sentPassword);
            params.put("description", description);
            sendRequest(this, getString(R.string.add_description_sub_url), params);
        }else{
            openUserProfile(sentUsername, sentEmail, sentPassword);
        }
    }

    private void openUserProfile(String username, String email, String password) {
        Intent intent = new Intent(SetDescriptionActivity.this, UserProfile.class);
        intent.putExtra("name", username);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        startActivity(intent);
    }

    public void setDescription(View view){
       setDescription();
    }

    @Override
    public void positiveResponseReaction(JSONObject response) {
        openUserProfile(sentUsername, sentEmail, sentPassword);
    }

    @Override
    public void negativeResponseReaction(JSONObject response) {
        Toast.makeText(this.getApplicationContext(), R.string.description_set_error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void errorReaction() {
        Toast.makeText(this.getApplicationContext(), R.string.description_set_error, Toast.LENGTH_LONG).show();
    }
}
