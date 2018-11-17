package com.miancky.hvislar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();

        TextView person = (TextView) findViewById(R.id.tPerson);
        String interlocutor = intent.getStringExtra("interlocutor");
        person.setText(interlocutor);
        establishServerConnection();
    }

    private void establishServerConnection(){

    }
}
