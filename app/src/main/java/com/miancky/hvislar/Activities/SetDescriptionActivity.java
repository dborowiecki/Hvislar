package com.miancky.hvislar.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.miancky.hvislar.R;
import com.miancky.hvislar.Complementary.UserProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SetDescriptionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_description);

    }

    private void setDescription(){
        final String GET_DESCRIPTION_URL = "http://" + getString(R.string.ip) + getString(R.string.port) + "/addDescription/";
        Intent intent = getIntent();
        final String username = intent.getStringExtra("name");
        final String email = intent.getStringExtra("email");
        final String password = intent.getStringExtra("password");
        final EditText descriptionField = findViewById(R.id.descriptionField);
        final String description = descriptionField.getText().toString().trim();

        if(!description.equals("")) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_DESCRIPTION_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject JSONResponse = new JSONObject(response);
                                if (!JSONResponse.getBoolean("success")) {
                                    Toast.makeText(SetDescriptionActivity.this, "There was a problem with your description.", Toast.LENGTH_LONG).show();
                                }else{
                                    openUserProfile(username, email, password);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(SetDescriptionActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("password", password);
                    params.put("description", description);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }else{
            openUserProfile(username, email, password);
        }
    }

    private void openUserProfile(String username, String email, String password) {
        Intent intent = new Intent(SetDescriptionActivity.this, UserProfile.class);
        intent.putExtra("name", username);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        startActivity(intent);
    }

    public void setDescription(View view){
       setDescription();
    }

}
