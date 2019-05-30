package com.miancky.hvislar.communication;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.miancky.hvislar.activities.ResponsiveActivity;

class ServerErrorListener implements Response.ErrorListener {

    private ResponsiveActivity activity;

    ServerErrorListener(ResponsiveActivity activity){
        this.activity = activity;
    }

    @Override
    public void onErrorResponse(VolleyError error) {

        activity.errorReaction();
    }
}
