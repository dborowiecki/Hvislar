package com.miancky.hvislar.Activities;

import android.os.Bundle;
import android.app.Activity;

import com.miancky.hvislar.R;

public class CheckNotificationsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_notifications);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
