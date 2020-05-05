
package com.techsole8.marocchat.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.techsole8.marocchat.R;
import com.techsole8.marocchat.adapter.UserActionListAdapter;
import com.techsole8.marocchat.model.Extra;


public class UserActivity extends ListActivity
{
    private String nickname;

    /**
     * On create
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.user);
        setListAdapter(new UserActionListAdapter());

        nickname = getIntent().getStringExtra(Extra.USER);
        ((TextView) findViewById(R.id.nickname)).setText(nickname);
    }

    /**
     * On action selected
     */
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id)
    {
        Intent intent = new Intent();
        intent.putExtra(Extra.ACTION, (int) id);
        intent.putExtra(Extra.USER, nickname);
        setResult(RESULT_OK, intent);
        finish();
    }
}
