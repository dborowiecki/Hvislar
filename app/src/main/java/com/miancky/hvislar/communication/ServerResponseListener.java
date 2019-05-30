package com.miancky.hvislar.communication;

import com.android.volley.Response;
import com.miancky.hvislar.activities.ResponsiveActivity;

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
           activity.errorReaction();
        }
    }
}
