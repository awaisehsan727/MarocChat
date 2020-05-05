
package com.techsole8.marocchat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.techsole8.marocchat.listener.ConversationListener;
import com.techsole8.marocchat.model.Broadcast;
import com.techsole8.marocchat.model.Extra;


public class ConversationReceiver extends BroadcastReceiver
{
    private final ConversationListener listener;
    private final int serverId;

    /**
     * Create a new channel receiver
     * 
     * @param serverId Only listen on channels of this server
     * @param listener
     */
    public ConversationReceiver(int serverId, ConversationListener listener)
    {
        this.listener = listener;
        this.serverId = serverId;
    }

    /**
     * On receive broadcast
     * 
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        int serverId = intent.getExtras().getInt(Extra.SERVER);
        if (serverId != this.serverId) {
            return;
        }

        String action = intent.getAction();

        if (action.equals(Broadcast.CONVERSATION_MESSAGE)) {
            listener.onConversationMessage(intent.getExtras().getString(Extra.CONVERSATION));
        } else if (action.equals(Broadcast.CONVERSATION_NEW)) {
            listener.onNewConversation(intent.getExtras().getString(Extra.CONVERSATION));
        } else if (action.equals(Broadcast.CONVERSATION_REMOVE)) {
            listener.onRemoveConversation(intent.getExtras().getString(Extra.CONVERSATION));
        } else if (action.equals(Broadcast.CONVERSATION_TOPIC)) {
            listener.onTopicChanged(intent.getExtras().getString(Extra.CONVERSATION));
        }

    }
}
