package com.miancky.hvislar.communication;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

class ServerRequest extends StringRequest {

    private Map<String, String> params;

    ServerRequest(int method, String url, Response.Listener<String> listener, @Nullable Response.ErrorListener errorListener, Map<String, String> params) {
        super(method, url, listener, errorListener);
        this.params = params;
    }

    @Override
    protected Map<String,String> getParams(){
        return params;
    }
}
