package com.miancky.hvislar;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
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

import java.io.IOException;
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

        final EditText eLogin = (EditText) findViewById(R.id.tName);
        final EditText eEmail = (EditText) findViewById(R.id.tEmail);
        final EditText ePassword = (EditText) findViewById(R.id.tPassword);
        final Button signUp= (Button) findViewById(R.id.bRegister);

    }

    public void goToLoginScreen(View view){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void registerUser(){
        final EditText eLogin = (EditText) findViewById(R.id.tName);
        final EditText eEmail = (EditText) findViewById(R.id.tEmail);
        final EditText ePassword = (EditText) findViewById(R.id.tPassword);
        final String username = eLogin.getText().toString().trim();
        final String password = eEmail.getText().toString().trim();
        final String email = ePassword.getText().toString().trim();

        //URL IN ASSETS FILE CALLED CONFIG
        //TODO: Create private asset config file (with JSON object?) and class to get data from it
        try {
            AssetManager am = getApplicationContext().getAssets();
            InputStream is = am.open("private/CONFIG");
            final String REGISTER_URL = convert(is, Charset.defaultCharset())+"register.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(RegisterActivity.this,response,Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this,error.toString()+REGISTER_URL,Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("name",username);
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
    public String convert(InputStream inputStream, Charset charset) throws IOException {
        Scanner scanner = new Scanner(inputStream, charset.name());
        return scanner.useDelimiter("\\A").next();
    }

}