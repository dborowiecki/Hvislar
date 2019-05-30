package com.miancky.hvislar.communication;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.miancky.hvislar.activities.ResponsiveActivity;
import com.miancky.hvislar.R;

import java.util.Map;

public class ServerCommunicator {
    private static RequestQueue requestQueue;

    public static void sendRequest(ResponsiveActivity activity, String subURL, Map<String, String> params){
        Context context = activity.getApplicationContext();
        ServerRequest serverRequest = new ServerRequest(Request.Method.POST, "http://"+context.getString(R.string.ip)+context.getString(R.string.port) + "/" + subURL + "/",
                new ServerResponseListener(activity), new ServerErrorListener(activity), params);
        getRequestQueue(context).add(serverRequest);
    }

    private static RequestQueue getRequestQueue(Context context){
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        return requestQueue;
    }
}
