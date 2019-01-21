package com.miancky.hvislar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;
public class ListOfFriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_friends);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");

        ListView listOfFriends = findViewById(R.id.lvFriends);
        // create the ArrayList to store the titles of nodes
        final ArrayList<String> listItems = new ArrayList<>();
        //TODO: should take friends from db
        getListOfFriends(new VolleyCallback() {
            @Override
            public void onSuccess(List<String> result) {
                listItems.addAll(result);
            }
        });
        ArrayAdapter<String> ad = new ArrayAdapter<>(ListOfFriendsActivity.this, android.R.layout.simple_list_item_1, listItems);

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

    public void goAsyncChat(View view){
        getRoomName();
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
    private void getListOfFriends(final VolleyCallback callback){
        try {
            final String URL = "http://" + getString(R.string.ip) + getString(R.string.port) + "/getFriendList/";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            List<String> receivedUsers = new ArrayList<>();
                            try {
                                JSONObject JSONResponse = new JSONObject(response);
                                if(!JSONResponse.getBoolean("success")){
                                    Toast.makeText(ListOfFriendsActivity.this,"Fetching friends list failed",Toast.LENGTH_LONG).show();
                                }
                                else{
                                    JSONArray friends = JSONResponse.getJSONArray("contacts");
                                    for(int i = 0; i < friends.length(); i++)
                                        receivedUsers.add(friends.getString(i));
                                    callback.onSuccess(receivedUsers);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ListOfFriendsActivity.this,error.toString()+URL,Toast.LENGTH_LONG).show();
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
            Toast.makeText(ListOfFriendsActivity.this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    private void getRoomName() {
        Intent intent = getIntent();

        final String GET_DESCRIPTION_URL = "http://" + getString(R.string.ip) + getString(R.string.port) + "/joinNewMassConversation/";
        final String password = intent.getStringExtra("password");
        final String email = intent.getStringExtra("email");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_DESCRIPTION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject JSONResponse = new JSONObject(response);
                            if (!JSONResponse.getBoolean("success"))
                                Toast.makeText(ListOfFriendsActivity.this, "Fail fining room", Toast.LENGTH_LONG).show();
                            else {
                                Intent intent = new Intent(ListOfFriendsActivity.this, AsyncChatActActivity.class);
                                intent.putExtra("name",     getIntent().getStringExtra("name"));
                                intent.putExtra("password", getIntent().getStringExtra("password"));
                                intent.putExtra("email",    getIntent().getStringExtra("email"));
                                intent.putExtra("roomName",    JSONResponse.getString("room_name"));
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ListOfFriendsActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("password", password);
                params.put("email", email);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    //TODO: refactor, move to another class
    public String convert(InputStream inputStream, Charset charset) {
        Scanner scanner = new Scanner(inputStream, charset.name());
        return scanner.useDelimiter("\\A").next();
    }
}
