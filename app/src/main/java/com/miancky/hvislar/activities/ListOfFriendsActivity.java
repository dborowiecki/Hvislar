package com.miancky.hvislar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.miancky.hvislar.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.miancky.hvislar.communication.ServerCommunicator.sendRequest;

public class ListOfFriendsActivity extends ResponsiveActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_friends);
        getListOfFriends();
    }

    private void showListOfFriends(final List<String> listItems){
        ListView listOfFriends = findViewById(R.id.lvFriends);
        ArrayAdapter<String> ad = new ArrayAdapter<>(ListOfFriendsActivity.this, android.R.layout.simple_list_item_1, listItems);

        listOfFriends.setAdapter(ad);

        listOfFriends.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                Intent intent = new Intent(ListOfFriendsActivity.this, ChatActivity.class);
                intent.putExtra("name", getIntent().getStringExtra("name"));
                intent.putExtra("email", getIntent().getStringExtra("email"));
                intent.putExtra("password", getIntent().getStringExtra("password"));
                intent.putExtra("friendName", listItems.get(position));
                startActivity(intent);
            }
        });
    }

    public void goToAddingFriends(View view){
        Intent intent = new Intent(ListOfFriendsActivity.this, AddNewFriendsActivity.class);
        intent.putExtra("name", getIntent().getStringExtra("name"));
        intent.putExtra("email", getIntent().getStringExtra("email"));
        intent.putExtra("password", getIntent().getStringExtra("password"));
        startActivity(intent);
    }

    private void getListOfFriends(){
        Map<String,String> params = new HashMap<>();
        Intent intent = getIntent();
        params.put("email", intent.getStringExtra("email"));
        params.put("password", intent.getStringExtra("password"));
        sendRequest(this,getString(R.string.friend_list_sub_url),params);
    }

    @Override
    public void positiveResponseReaction(JSONObject response) {
        try {
            JSONArray friends;
            List<String> receivedUsers = new ArrayList<>();
            friends = response.getJSONArray("contacts");
            for (int i = 0; i < friends.length(); i++)
                receivedUsers.add(friends.getString(i));
            showListOfFriends(receivedUsers);
        } catch (JSONException e) {
            errorReaction();
        }
    }

    @Override
    public void negativeResponseReaction(JSONObject response) {
        Toast.makeText(this,R.string.friend_list_error,Toast.LENGTH_LONG).show();
    }

    @Override
    public void errorReaction() {
        Toast.makeText(this.getApplicationContext(),R.string.processing_error,Toast.LENGTH_LONG).show();
    }
}
