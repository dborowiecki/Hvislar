package com.miancky.hvislar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ListOfFriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_friends);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");

    }

    public void goToUserProfile(View view){
        Intent intent = new Intent(ListOfFriendsActivity.this, UserProfile.class);
        intent.putExtra("name", getIntent().getStringExtra("name"));
        intent.putExtra("email", intent.getStringExtra("email"));
        startActivity(intent);
    }

}
