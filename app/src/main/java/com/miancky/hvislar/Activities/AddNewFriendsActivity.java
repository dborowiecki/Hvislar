package com.miancky.hvislar.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.miancky.hvislar.R;
import com.miancky.hvislar.Complementary.UserListAdapter;
import com.miancky.hvislar.Complementary.UserListAdapter2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
interface VolleyCallback{
    void onSuccess(List<String> result);
}
public class AddNewFriendsActivity extends AppCompatActivity {
    int actualView = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_friends);

       Intent intent = getIntent();
       String name = intent.getStringExtra("name");
       String email = intent.getStringExtra("email");

        fetchUsersFromDb();
    }

    public void refresh(View view){
//       // getUserList(userList);
//        Toast.makeText(AddNewFriendsActivity.this, userList.toString(), Toast.LENGTH_LONG).show();
//
//        Toast.makeText(AddNewFriendsActivity.this, "refreshing", Toast.LENGTH_LONG).show();
    }

    public void fetchUsersFromDb(){
        getUserList();
    }

    private void getUserList(){
        try {
            final String URL = "http://" + getString(R.string.ip) + getString(R.string.port) + "/getPotentialFriends/";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ArrayList<String> receivedUsers = new ArrayList<>();
                            try {
                                JSONObject JSONResponse = new JSONObject(response);
                                if(!JSONResponse.getBoolean("success")){
                                    Toast.makeText(AddNewFriendsActivity.this,"Fetching potential friends failed",Toast.LENGTH_LONG).show();
                                }
                                else{
                                    JSONArray potentialFriends = JSONResponse.getJSONArray("accounts");
                                    for(int i = 0; i < potentialFriends.length(); i++)
                                        receivedUsers.add(potentialFriends.getString(i));
                                    showPotentialFriends(receivedUsers);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(AddNewFriendsActivity.this,error.toString()+URL,Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<>();
                    Intent intent = getIntent();
                    params.put("email", intent.getStringExtra("email"));
                    params.put("password", intent.getStringExtra("password"));
                    return params;
                }

            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        } catch (Exception e){
            Toast.makeText(AddNewFriendsActivity.this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    private void getInvitations(){
        try {
            final String URL = "http://" + getString(R.string.ip) + getString(R.string.port) + "/getInvitations/";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ArrayList<String> receivedUsers = new ArrayList<>();
                            try {
                                JSONObject JSONResponse = new JSONObject(response);
                                if(!JSONResponse.getBoolean("success")){
                                    Toast.makeText(AddNewFriendsActivity.this,"Fetching potential friends failed",Toast.LENGTH_LONG).show();
                                    Toast.makeText(AddNewFriendsActivity.this,JSONResponse.getString("error"),Toast.LENGTH_LONG).show();
                                }
                                else{
                                    JSONArray potentialFriends = JSONResponse.getJSONArray("invitations");
                                    for(int i = 0; i < potentialFriends.length(); i++)
                                        receivedUsers.add(potentialFriends.getString(i));
                                    showMyInvitations(receivedUsers);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(AddNewFriendsActivity.this,error.toString()+URL,Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<>();
                    Intent intent = getIntent();
                    params.put("email", intent.getStringExtra("email"));
                    params.put("password", intent.getStringExtra("password"));
                    return params;
                }

            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        } catch (Exception e){
            Toast.makeText(AddNewFriendsActivity.this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    private void showPotentialFriends(ArrayList<String> potentialFriends){
        actualView = 0;
        ListView listOfPotentialFriends = findViewById(R.id.lvUsers);
        UserListAdapter ad = new UserListAdapter(AddNewFriendsActivity.this, potentialFriends);

        listOfPotentialFriends.setAdapter(ad);

        listOfPotentialFriends.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                Toast.makeText(AddNewFriendsActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMyInvitations(ArrayList<String> potentialFriends){
        actualView = 1;
        ListView listOfPotentialFriends = findViewById(R.id.lvUsers);
        UserListAdapter2 ad = new UserListAdapter2(AddNewFriendsActivity.this, potentialFriends);

        listOfPotentialFriends.setAdapter(ad);

        listOfPotentialFriends.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                Toast.makeText(AddNewFriendsActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void switchView(View view){
        if(actualView == 0){
            getInvitations();
        }
        else
            getUserList();
    }

    //TODO: refactor, move to another class
    public String convert(InputStream inputStream, Charset charset) throws IOException {
        Scanner scanner = new Scanner(inputStream, charset.name());
        return scanner.useDelimiter("\\A").next();
    }

    public void addFriend(View view){

    }
}
