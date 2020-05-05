
package com.techsole8.marocchat.listener;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.TextView;

import com.techsole8.marocchat.adapter.ConversationPagerAdapter;
import com.techsole8.marocchat.irc.IRCService;
import com.techsole8.marocchat.model.Channel;
import com.techsole8.marocchat.model.Conversation;
import com.techsole8.marocchat.model.Server;


public class ConversationSelectedListener implements OnPageChangeListener
{
    private final Context context;
    private final Server server;
    private final TextView titleView;
    private final ConversationPagerAdapter adapter;

    /**
     * Create a new ConversationSelectedListener
     *
     * @param server
     * @param titleView
     */
    public ConversationSelectedListener(Context ctx, Server server, TextView titleView, ConversationPagerAdapter adapter)
    {
        this.context       = ctx;
        this.server    = server;
        this.titleView = titleView;
        this.adapter   = adapter;
    }

    /**
     * On page has been selected.
     */
    @Override
    public void onPageSelected(int position)
    {
        Conversation conversation = adapter.getItem(position);

        if (conversation != null && conversation.getType() != Conversation.TYPE_SERVER) {
            StringBuilder sb = new StringBuilder();
            sb.append(server.getTitle() + " - " + conversation.getName());
            if (conversation.getType() == Conversation.TYPE_CHANNEL && !((Channel)conversation).getTopic().equals("")) {
                sb.append(" - " + ((Channel)conversation).getTopic());
            }
            titleView.setText(sb.toString());
        } else {
            titleView.setText(server.getTitle());
        }

        // Remember selection
        if (conversation != null) {
            Conversation previousConversation = server.getConversation(server.getSelectedConversation());

            if (previousConversation != null) {
                previousConversation.setStatus(Conversation.STATUS_DEFAULT);
            }

            if (conversation.getNewMentions() > 0) {
                Intent i = new Intent(context, IRCService.class);
                i.setAction(IRCService.ACTION_ACK_NEW_MENTIONS);
                i.putExtra(IRCService.EXTRA_ACK_SERVERID, server.getId());
                i.putExtra(IRCService.EXTRA_ACK_CONVTITLE, conversation.getName());
                context.startService(i);
            }

            conversation.setStatus(Conversation.STATUS_SELECTED);
            server.setSelectedConversation(conversation.getName());
        }
    }

    /**
     * On scroll state of pager has been chanaged.
     */
    @Override
    public void onPageScrollStateChanged(int state)
    {
    }

    /**
     * On page has been scrolled.
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {
    }
}
