package com.miancky.hvislar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class UserProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        final TextView username = (TextView) findViewById(R.id.tUsername);
        final TextView welcomeMessage = (TextView) findViewById(R.id.tHelloMessage);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");

        String welcomeText = "Hello, "+name;

        welcomeMessage.setText(welcomeText);
        username.setText(name);
    }
}
