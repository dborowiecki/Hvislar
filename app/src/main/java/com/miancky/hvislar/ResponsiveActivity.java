package com.miancky.hvislar;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

abstract class ResponsiveActivity extends AppCompatActivity {
    abstract void positiveResponseReaction(JSONObject response);
    abstract void negativeResponseReaction(JSONObject response);
    abstract void errorReaction();
}
