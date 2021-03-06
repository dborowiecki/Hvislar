package com.miancky.hvislar.complementary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.miancky.hvislar.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

//TODO: refactor, move to another class
public class UserListInvitationAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;

    public UserListInvitationAdapter(Context context, ArrayList<String> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        //return list.get(pos).getId();
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.add_friend_list_row_layout, null);
        }

        //Handle TextView and display string from your list
        final TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position));

        //Handle buttons and add onClickListeners
        //Button deleteBtn = view.findViewById(R.id.delete_btn);
        Button accept = view.findViewById(R.id.add_btn);

//        deleteBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                list.remove(position); //or some other task
//                notifyDataSetChanged();
//            }
//        });
        accept.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(context, list.get(position), Toast.LENGTH_LONG).show();
                addNewFriend(list.get(position), position, true);
                notifyDataSetChanged();
            }
        });

        return view;
    }

    public void addNewFriend(final String newFriendName, int position, final boolean answer){
        final String ADD_FRIEND_URL = "http://" + context.getString(R.string.ip) +  context.getString(R.string.port) + "/answerContactRequest/";
        final int POSITION = position;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_FRIEND_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject JSONResponse = new JSONObject(response);
                            if (!JSONResponse.getBoolean("success"))
                                Toast.makeText(context, JSONResponse.getString("error"), Toast.LENGTH_LONG).show();
                            else {
                                Toast.makeText(context, "Friend added correctly.", Toast.LENGTH_LONG).show();
                                list.remove(POSITION);
                                notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Intent intent = ((Activity) context).getIntent();
                Map<String, String> params = new HashMap<>();
                String message = intent.getStringExtra("name")+" invites to join his contacts.";
                params.put("email", intent.getStringExtra("email"));
                params.put("password", intent.getStringExtra("password"));
                params.put("response", answer == true? "Accept" : "Deny");
                params.put("responsed_user", newFriendName);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
    //TODO: refactor, move to another class
    public String convert(InputStream inputStream, Charset charset) throws IOException {
        Scanner scanner = new Scanner(inputStream, charset.name());
        return scanner.useDelimiter("\\A").next();
    }
}