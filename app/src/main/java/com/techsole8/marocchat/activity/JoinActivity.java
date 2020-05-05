
package com.techsole8.marocchat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.techsole8.marocchat.R;


public class JoinActivity extends Activity implements OnClickListener
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.join);

        ((Button) findViewById(R.id.join)).setOnClickListener(this);

        ((EditText) findViewById(R.id.channel)).setSelection(1);
    }


    @Override
    public void onClick(View v)
    {
        Intent intent = new Intent();
        intent.putExtra("channel", ((EditText) findViewById(R.id.channel)).getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}
