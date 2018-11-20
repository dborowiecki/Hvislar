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
import java.util.LinkedList;
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
        final ArrayList<String> listItems = new ArrayList<String>();
        //TODO: should take friends from db
        getListOfFriends(new VolleyCallback() {
            @Override
            public void onSuccess(List<String> result) {
                listItems.addAll(result);
            }
        });
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
        Toast.makeText(ListOfFriendsActivity.this, listItems.toString(), Toast.LENGTH_SHORT).show();
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
    public void getListOfFriends(final VolleyCallback callback){
        try {
            AssetManager am = getApplicationContext().getAssets();
            InputStream is = am.open("private/CONFIG");
            final String LOGIN_URL = convert(is, Charset.defaultCharset())+"getUserFriends.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            List<String> recivedUsers = new ArrayList<>();
                            try {
                                JSONObject JSONresponse = new JSONObject(response);
                                if(!JSONresponse.getBoolean("success")){
                                    Toast.makeText(ListOfFriendsActivity.this,"Fetching friends list failed",Toast.LENGTH_LONG).show();
                                }
                                else{
                                    JSONArray friends = JSONresponse.getJSONArray("users");
                                    for(int i = 0; i < friends.length(); i++){
                                        recivedUsers.add(friends.getString(i));
                                        Toast.makeText(ListOfFriendsActivity.this,"hello",Toast.LENGTH_LONG).show();
                                    }
                                    Toast.makeText(ListOfFriendsActivity.this,"hello",Toast.LENGTH_LONG).show();
                                    callback.onSuccess(recivedUsers);
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
                    params.put("username", getIntent().getStringExtra("name"));
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
