package com.miancky.hvislar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void goToRegisterScreen(View view){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void goAsyncChat(View view){
        Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
        startActivity(intent);
    }

    private void loginUser(){
        final EditText eEmail    = findViewById(R.id.tEmail);
        final EditText ePassword = findViewById(R.id.tPassword);
        final String password    = ePassword.getText().toString().trim();
        final String email       = eEmail.getText().toString().toLowerCase().trim();

        //URL IN ASSETS FILE CALLED CONFIG
        //TODO: Create private asset config file (with JSON object?) and class to get data from it
        try {
            final String LOGIN_URL = "http://"+getString(R.string.ip)+ getString(R.string.port) + "/login/";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(LoginActivity.this,response,Toast.LENGTH_LONG).show();
                            try {
                                JSONObject JSONResponse = new JSONObject(response);
                                if(!JSONResponse.getBoolean("success")){
                                    Toast.makeText(LoginActivity.this,"Login failed, incorrect data :)",Toast.LENGTH_LONG).show();
                                }
                                else{
                                    String name = JSONResponse.getString("name");
                                    String email = JSONResponse.getString("email");

                                    Intent intent = new Intent(LoginActivity.this, UserProfile.class);
                                    intent.putExtra("name", name);
                                    intent.putExtra("email", email);
                                    intent.putExtra("password", password);
                                    startActivity(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(LoginActivity.this,error.toString()+LOGIN_URL,Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<>();
                    params.put("password",password);
                    params.put("email", email);
                    return params;
                }

            };


            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        } catch (Exception e){
            Toast.makeText(LoginActivity.this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    public void login(View view) {
        loginUser();
    }

    //TODO: refactor, move to another class
    public String convert(InputStream inputStream, Charset charset) {
        Scanner scanner = new Scanner(inputStream, charset.name());
        return scanner.useDelimiter("\\A").next();
    }

}
