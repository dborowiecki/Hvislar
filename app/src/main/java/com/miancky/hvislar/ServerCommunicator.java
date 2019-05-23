package com.miancky.hvislar;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.Map;

class ServerCommunicator {
    private static RequestQueue requestQueue;

    static void sendRequest(ResponsiveActivity activity, String subURL, Map<String, String> params){
        Context context = activity.getApplicationContext();
        ServerRequest serverRequest = new ServerRequest(Request.Method.POST, "http://"+context.getString(R.string.ip)+context.getString(R.string.port) + "/" + subURL + "/",
                new ServerResponseListener(activity), new ServerErrorListener(activity), params);
        getRequestQueue(context).add(serverRequest);
    }

    private static RequestQueue getRequestQueue(Context context){
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(context);
        return requestQueue;
    }
}
