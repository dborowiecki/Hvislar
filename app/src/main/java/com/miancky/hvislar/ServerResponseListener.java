package com.miancky.hvislar;

import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

class ServerResponseListener implements Response.Listener<String> {

    private ResponsiveActivity activity;

    ServerResponseListener(ResponsiveActivity activity){
        this.activity = activity;
    }

    @Override
    public void onResponse(String response) {
        try {
            JSONObject JSONResponse = new JSONObject(response);
            if (JSONResponse.getBoolean("success"))
                activity.positiveResponseReaction(JSONResponse);
            else
                activity.negativeResponseReaction(JSONResponse);
        } catch (JSONException e) {
            Toast.makeText(activity,"Login failed. Incorrect data!",Toast.LENGTH_LONG).show();
        }
    }
}
