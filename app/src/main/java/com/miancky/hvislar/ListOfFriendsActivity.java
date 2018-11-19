package com.miancky.hvislar;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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

    public void goToAddingFriends(View view){
        Intent intent = new Intent(ListOfFriendsActivity.this, AddNewFriendsActivity.class);
        intent.putExtra("name", getIntent().getStringExtra("name"));
        intent.putExtra("email", intent.getStringExtra("email"));
        startActivity(intent);
    }
    public void getListOfFriends(View view){
        try {
            AssetManager am = getApplicationContext().getAssets();
            InputStream is = am.open("private/CONFIG");
            final String LOGIN_URL = convert(is, Charset.defaultCharset())+"getAllUsers.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(ListOfFriendsActivity.this,response,Toast.LENGTH_LONG).show();
                            try {
                                JSONObject JSONresponse = new JSONObject(response);
                                if(!JSONresponse.getBoolean("success")){
                                    Toast.makeText(ListOfFriendsActivity.this,"Fetching friends list failed",Toast.LENGTH_LONG).show();
                                }
                                else{
                                    JSONArray friends = JSONresponse.getJSONArray("users");
                                    List<String> list = new ArrayList<String>();
                                    for(int i = 0; i < friends.length(); i++){
                                        list.add(friends.getJSONObject(i).getString("name"));
                                    }
                                    Toast.makeText(ListOfFriendsActivity.this,list.toString(),Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ListOfFriendsActivity.this,error.toString()+LOGIN_URL,Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    return params;
                }

            };


            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        } catch (Exception e){
            Toast.makeText(ListOfFriendsActivity.this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }
    //TODO: refactor, move to another class
    public String convert(InputStream inputStream, Charset charset) throws IOException {
        Scanner scanner = new Scanner(inputStream, charset.name());
        return scanner.useDelimiter("\\A").next();
    }
}
