package com.miancky.hvislar;

import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
interface VolleyCallback{
    void onSuccess(List<String> result);
}
public class AddNewFriendsActivity extends AppCompatActivity {

    List<String> userList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_friends);

       Intent intent = getIntent();
       String name = intent.getStringExtra("name");
       String email = intent.getStringExtra("email");

        ListView listOfFriends = (ListView)findViewById(R.id.lvUsers);
        // create the ArrayList to store the titles of nodes
        final ArrayList<String> listItems = new ArrayList<String>();
        //TODO: should take friends from db
        listItems.add("test");
        fetchUsersFromDb(null);
        getUserList(new VolleyCallback() {
            @Override
            public void onSuccess(List<String> result) {
                listItems.addAll(result);
                //Toast.makeText(AddNewFriendsActivity.this, userList.toString(), Toast.LENGTH_LONG).show();
            }
        });
        listItems.addAll(userList);
        UserListAdapter ad = new UserListAdapter(AddNewFriendsActivity.this, listItems);

        // give adapter to ListView UI element to render
        listOfFriends.setAdapter(ad);

    }



    public void goToFriendList(View view){
        Intent intent = new Intent(AddNewFriendsActivity.this, ListOfFriendsActivity.class);
        intent.putExtra("name", getIntent().getStringExtra("name"));
        intent.putExtra("email", intent.getStringExtra("email"));
        startActivity(intent);
    }

    public void refresh(View view){
       // getUserList(userList);
        Toast.makeText(AddNewFriendsActivity.this, userList.toString(), Toast.LENGTH_LONG).show();

        Toast.makeText(AddNewFriendsActivity.this, "refreshing", Toast.LENGTH_LONG).show();
    }

    public void fetchUsersFromDb(View view){
     //   getUserList(userList);
    }
    public void getUserList(final VolleyCallback callback){
        try {
            AssetManager am = getApplicationContext().getAssets();
            InputStream is = am.open("private/CONFIG");
            final List<String> users = new LinkedList<>();
            final String LOGIN_URL = convert(is, Charset.defaultCharset())+"getAllUsers.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                    new Response.Listener<String>() {
                        List<String> recivedUsers = new ArrayList<>();
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject JSONresponse = new JSONObject(response);
                                if(!JSONresponse.getBoolean("success")){
                                    Toast.makeText(AddNewFriendsActivity.this,"Fetching friends list failed",Toast.LENGTH_LONG).show();
                                }
                                else{
                                    JSONArray friends = JSONresponse.getJSONArray("users");
                                    for(int i = 0; i < friends.length(); i++){
                                        recivedUsers.add(friends.getString(i));
                                    }
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
                            Toast.makeText(AddNewFriendsActivity.this,error.toString()+LOGIN_URL,Toast.LENGTH_LONG).show();
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
            Toast.makeText(AddNewFriendsActivity.this,e.toString(),Toast.LENGTH_LONG).show();
        }

    }
    //TODO: refactor, move to another class
    public String convert(InputStream inputStream, Charset charset) throws IOException {
        Scanner scanner = new Scanner(inputStream, charset.name());
        return scanner.useDelimiter("\\A").next();
    }
}
