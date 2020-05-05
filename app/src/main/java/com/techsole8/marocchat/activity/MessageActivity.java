package com.techsole8.marocchat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.techsole8.marocchat.R;
import com.techsole8.marocchat.model.Extra;


public class MessageActivity extends Activity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.message);

        ((TextView) findViewById(R.id.message)).setText(
            getIntent().getExtras().getString(Extra.MESSAGE)
        );
    }
}
