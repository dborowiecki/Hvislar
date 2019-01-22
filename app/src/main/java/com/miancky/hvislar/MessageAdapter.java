package com.miancky.hvislar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

//TODO: refactor, move to another class
public class MessageAdapter extends BaseAdapter implements ListAdapter {
    private List<String> messages = new ArrayList<String>();
    private Context context;
    private List<String> users;

    public MessageAdapter(Context context, List<String> messages, List<String> users) {
        this.messages = messages;
        this.context = context;
        this.users = users;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int pos) {
        return messages.get(pos);
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
        final TextView listItemText;
        Intent intent = ((Activity) context).getIntent();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(users.get(position).equals("you")) {
                view = inflater.inflate(R.layout.sent_message_layout, null);
                listItemText = view.findViewById(R.id.tSent);
            }else{
                view = inflater.inflate(R.layout.received_message_layout, null);
                listItemText = view.findViewById(R.id.tReceived);
            }

        //Handle TextView and display string from your list
        listItemText.setText(messages.get(position));

        return view;
    }
}