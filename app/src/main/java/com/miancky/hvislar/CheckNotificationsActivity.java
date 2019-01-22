package com.miancky.hvislar;

import android.os.Bundle;
import android.app.Activity;

public class CheckNotificationsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_notifications);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
