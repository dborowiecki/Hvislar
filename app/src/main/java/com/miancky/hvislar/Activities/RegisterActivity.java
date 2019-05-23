package com.miancky.hvislar.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.miancky.hvislar.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText eLogin = findViewById(R.id.tName);
        final EditText eEmail = findViewById(R.id.tEmail);
        final EditText ePassword = findViewById(R.id.tPassword);
        final Button signUp= findViewById(R.id.bRegister);

    }

    public void goToLoginScreen(View view){
        finish();
    }

    private void registerUser(){
        final EditText eLogin = findViewById(R.id.tName);
        final EditText eEmail = findViewById(R.id.tEmail);
        final EditText ePassword = findViewById(R.id.tPassword);
        final String username = eLogin.getText().toString().trim();
        final String password = ePassword.getText().toString().trim();
        final String email = eEmail.getText().toString().toLowerCase().trim();

        Toast.makeText(RegisterActivity.this, email,Toast.LENGTH_LONG).show();
        //URL IN ASSETS FILE CALLED CONFIG
        //TODO: Create private asset config file (with JSON object?) and class to get data from it
        try {
            final String REGISTER_URL = "http://" + getString(R.string.ip) + getString(R.string.port) + "/register/";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject JSONResponse=new JSONObject(response);
                            if(JSONResponse.getBoolean("success")){
                                Toast.makeText(RegisterActivity.this,getString(R.string.registration_completed),Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(RegisterActivity.this, SetDescriptionActivity.class);
                                intent.putExtra("name", username);
                                intent.putExtra("email", email);
                                intent.putExtra("password", password);
                                startActivity(intent);
                            }else{
                                Toast.makeText(RegisterActivity.this,getString(R.string.registration_failed),Toast.LENGTH_LONG).show();
                            }
                        } catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("Volly Error", error.toString());

                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null) {
                            Log.e("Status code", String.valueOf(networkResponse.statusCode));
                        }
                    }
                    /*@Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this,error.toString()+REGISTER_URL,Toast.LENGTH_LONG).show();
                    }*/
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("username",username);
                params.put("password",password);
                params.put("email", email);
                return params;
            }

        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        } catch (Exception e){
            Toast.makeText(RegisterActivity.this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    public void register(View view) {
            registerUser();
    }

    //TODO: refactor, move to another class
    public String convert(InputStream inputStream, Charset charset) {
        Scanner scanner = new Scanner(inputStream, charset.name());
        return scanner.useDelimiter("\\A").next();
    }

}
