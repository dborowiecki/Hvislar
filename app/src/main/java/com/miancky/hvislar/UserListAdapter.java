package com.miancky.hvislar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
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
public class UserListAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;

    public UserListAdapter(Context context, ArrayList<String> list) {
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
        Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);
        Button addBtn = (Button)view.findViewById(R.id.add_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                list.remove(position); //or some other task
                notifyDataSetChanged();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                //TODO: addNewFriend should be in user service class
                Toast.makeText(context, list.get(position), Toast.LENGTH_LONG).show();
                addNewFriend(list.get(position));
                notifyDataSetChanged();
            }
        });

        return view;
    }

    public void addNewFriend(final String newFriendName){
        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open("private/CONFIG");
            final String LOGIN_URL = convert(is, Charset.defaultCharset())+"addFriend.php";
            final Intent intent = ((Activity) context).getIntent();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                            try {
                                JSONObject JSONresponse = new JSONObject(response);
                                if(!JSONresponse.getBoolean("success")){
                                    Toast.makeText(context,"Adding friend failed :)",Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(context,"Friend added correctly :)",Toast.LENGTH_LONG).show();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context,error.toString()+LOGIN_URL,Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("username", intent.getStringExtra("name"));
                    //TODO: incorrect fetching friend name
                    params.put("friend_name", newFriendName);
                    return params;
                }

            };


            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);
        } catch (Exception e){
            Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show();
        }
    }
    //TODO: refactor, move to another class
    public String convert(InputStream inputStream, Charset charset) throws IOException {
        Scanner scanner = new Scanner(inputStream, charset.name());
        return scanner.useDelimiter("\\A").next();
    }
}