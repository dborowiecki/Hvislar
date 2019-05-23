package com.miancky.hvislar;

import com.android.volley.Response;
import com.android.volley.VolleyError;

public class ServerErrorListener implements Response.ErrorListener {

    private ResponsiveActivity activity;

    ServerErrorListener(ResponsiveActivity activity){
        this.activity = activity;
    }

    @Override
    public void onErrorResponse(VolleyError error) {

        activity.errorReaction();
    }
}
