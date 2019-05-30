package com.miancky.hvislar.activities;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public abstract class ResponsiveActivity extends AppCompatActivity {
    public abstract void positiveResponseReaction(JSONObject response);
    public abstract void negativeResponseReaction(JSONObject response);
    public abstract void errorReaction();
}
