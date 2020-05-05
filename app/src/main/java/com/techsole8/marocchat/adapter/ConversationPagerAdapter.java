
package com.techsole8.marocchat.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.techsole8.marocchat.listener.MessageClickListener;
import com.techsole8.marocchat.model.Conversation;
import com.techsole8.marocchat.model.Server;
import com.techsole8.marocchat.view.MessageListView;

import java.util.HashMap;
import java.util.LinkedList;


public class ConversationPagerAdapter extends PagerAdapter
{
    private final Server server;
    private LinkedList<ConversationInfo> conversations;
    private final HashMap<Integer, View> views;

    public class ConversationInfo {
        public Conversation conv;
        public MessageListAdapter adapter;
        public MessageListView view;

        public ConversationInfo(Conversation conv) {
            this.conv = conv;
            this.adapter = null;
            this.view = null;
        }
    }

    public ConversationPagerAdapter(Context context, Server server) {
        this.server = server;

        conversations = new LinkedList<ConversationInfo>();
        views = new HashMap<Integer, View>();
    }

    public void addConversation(Conversation conversation) {
        conversations.add(new ConversationInfo(conversation));

        notifyDataSetChanged();
    }

    public void removeConversation(int position) {
        conversations.remove(position);

        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object)
    {
        if (views.containsKey(object)) {
            return POSITION_UNCHANGED;
        }

        return POSITION_NONE;
    }

    public Conversation getItem(int position)
    {
        ConversationInfo convInfo = getItemInfo(position);
        if (convInfo != null) {
            return convInfo.conv;
        } else {
            return null;
        }
    }

    public MessageListAdapter getItemAdapter(int position)
    {
        ConversationInfo convInfo = getItemInfo(position);
        if (convInfo != null) {
            return convInfo.adapter;
        } else {
            return null;
        }
    }

    public MessageListAdapter getItemAdapter(String name)
    {
        return getItemAdapter(getPositionByName(name));
    }

    private ConversationInfo getItemInfo(int position) {
        if (position >= 0 && position < conversations.size()) {
            return conversations.get(position);
        }
        return null;
    }

    public int getPositionByName(String name)
    {
        // Optimization - cache field lookups
        int mSize = conversations.size();
        LinkedList<ConversationInfo> mItems = this.conversations;

        for (int i = 0; i <  mSize; i++) {
            if (mItems.get(i).conv.getName().equalsIgnoreCase(name)) {
                return i;
            }
        }

        return -1;
    }

    public void clearConversations()
    {
        conversations = new LinkedList<ConversationInfo>();
    }

    @Override
    public int getCount()
    {
        return conversations.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }

    @Override
    public Object instantiateItem(View collection, int position) {
        ConversationInfo convInfo = conversations.get(position);
        View view;

        if (convInfo.view != null) {
            view = convInfo.view;
        } else {
            view = renderConversation(convInfo, collection);
        }

        views.put(position, view);
        ((ViewPager) collection).addView(view);

        return view;
    }


    private MessageListView renderConversation(ConversationInfo convInfo, View parent)
    {
        MessageListView list = new MessageListView(parent.getContext());
        convInfo.view = list;
        list.setOnItemClickListener(MessageClickListener.getInstance());

        MessageListAdapter adapter = convInfo.adapter;

        if (adapter == null) {
            adapter = new MessageListAdapter(convInfo.conv, parent.getContext());
            convInfo.adapter = adapter;
        }


        list.setAdapter(adapter);
        list.setSelection(adapter.getCount() - 1); // scroll to bottom

        return list;
    }


    @Override
    public void destroyItem(View collection, int position, Object view) {
        ((ViewPager) collection).removeView((View) view);
        views.remove(position);
    }

    @Override
    public String getPageTitle(int position)
    {
        Conversation conversation = getItem(position);

        if (conversation.getType() == Conversation.TYPE_SERVER) {
            return server.getTitle();
        } else {
            return conversation.getName();
        }
    }
}
