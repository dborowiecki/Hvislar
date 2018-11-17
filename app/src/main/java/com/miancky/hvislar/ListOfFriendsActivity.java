package com.miancky.hvislar;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListOfFriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_friends);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");

        ListView listOfFriends = (ListView)findViewById(R.id.lvFriends);
        // create the ArrayList to store the titles of nodes
        ArrayList<String> listItems = new ArrayList<String>();
        //TODO: should take friends from db
        listItems.add("Test1");
        listItems.add("Test2");
        ArrayAdapter ad = new ArrayAdapter(ListOfFriendsActivity.this,
                android.R.layout.simple_list_item_1, listItems);

        // give adapter to ListView UI element to render
        listOfFriends.setAdapter(ad);

        listOfFriends.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            //TODO: should open conversation with friend
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                Toast.makeText(ListOfFriendsActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void goToUserProfile(View view){
        Intent intent = new Intent(ListOfFriendsActivity.this, UserProfile.class);
        intent.putExtra("name", getIntent().getStringExtra("name"));
        intent.putExtra("email", intent.getStringExtra("email"));
        startActivity(intent);
    }

}
