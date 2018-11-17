package com.miancky.hvislar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

    public void goToFriendListScreen(View view){
        Intent intent = new Intent(UserProfile.this, ListOfFriendsActivity.class);
        intent.putExtra("name", getIntent().getStringExtra("name"));
        intent.putExtra("email", intent.getStringExtra("email"));
        startActivity(intent);
    }

    public void goToTitleScreen(View view){
        Intent intent = new Intent(UserProfile.this, MainActivity.class);
        startActivity(intent);
    }
}
