
package com.techsole8.marocchat.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import com.techsole8.marocchat.R;
import com.techsole8.marocchat.model.Extra;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;


public class UsersActivity extends ListActivity implements OnItemClickListener
{
    /**
     * On create
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.users);

        final String[] users = getIntent().getExtras().getStringArray(Extra.USERS);
        getListView().setOnItemClickListener(this);

        // Add sorted list of users in own thread to avoid blocking UI
        // TODO: Move to a background task and show loading indicator while sorting
        Collections.sort(Arrays.asList(users), new Comparator<String>()
        {
            @Override
            public int compare(String o1, String o2) {
                return o2.compareTo(o1);
            }
        });
//        Arrays.sort(users, String.CASE_INSENSITIVE_ORDER);
        getListView().setAdapter(new ArrayAdapter<String>(UsersActivity.this, R.layout.useritem, users));
    }

    /**
     * On user selected
     */
    @Override
    public void onItemClick(AdapterView<?> list, View item, int position, long id)
    {
        Intent intent = new Intent();
        intent.putExtra(Extra.USER, (String) getListView().getAdapter().getItem(position));
        setResult(RESULT_OK, intent);
        finish();
    }
}
