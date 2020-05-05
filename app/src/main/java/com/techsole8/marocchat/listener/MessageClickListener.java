
package com.techsole8.marocchat.listener;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.techsole8.marocchat.activity.MessageActivity;
import com.techsole8.marocchat.adapter.MessageListAdapter;
import com.techsole8.marocchat.model.Extra;


public class MessageClickListener implements OnItemClickListener
{
    private static MessageClickListener instance;

    /**
     * Private constructor
     */
    private MessageClickListener()
    {
    }

    /**
     * Get global instance of message click listener
     * 
     * @return
     */
    public static synchronized MessageClickListener getInstance()
    {
        if (instance == null) {
            instance = new MessageClickListener();
        }

        return instance;
    }

    /**
     * On message item clicked
     */
    @Override
    public void onItemClick(AdapterView<?> group, View view, int position, long id)
    {
        MessageListAdapter adapter = (MessageListAdapter) group.getAdapter();

        Intent intent = new Intent(group.getContext(), MessageActivity.class);
        intent.putExtra(Extra.MESSAGE, adapter.getItem(position).getText().toString());
        group.getContext().startActivity(intent);
    }
}
