package com.techsole8.marocchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.techsole8.marocchat.R;
import com.techsole8.marocchat.model.User;


public class UserActionListAdapter extends BaseAdapter {
    /**
     * Action IDs
     */
    private final int[] actions = {

            User.ACTION_REPLY,
            User.ACTION_QUERY,
            User.ACTION_Whois,
            User.ACTION_OP,
            User.ACTION_DEOP,
            User.ACTION_VOICE,
            User.ACTION_DEVOICE,
            User.ACTION_KICK,
            User.ACTION_BAN,
            User.ACTION_IGNORE,
            User.ACTION_UNIGNORE
    };

    /**
     * Labels for actions
     */
    private final int[] labels = {
            R.string.user_action_reply,
            R.string.user_action_query,
            R.string.user_action_whois,
            R.string.user_action_op,
            R.string.user_action_deop,
            R.string.user_action_voice,
            R.string.user_action_devoice,
            R.string.user_action_kick,
            R.string.user_action_ban,
            R.string.user_action_ignore,
            R.string.user_action_unignore,
    };

    /**
     * Icons for actions
     */
    private final int[] icons = {
            R.drawable.action_reply,
            R.drawable.action_query,
            R.drawable.action_op,
            R.drawable.action_op,
            R.drawable.action_deop,
            R.drawable.action_voice,
            R.drawable.action_devoice,
            R.drawable.action_kick,
            R.drawable.action_ban,
            R.drawable.action_ignore,
            R.drawable.action_unignore

    };

    /**
     * Get number of actions
     */
    @Override
    public int getCount() {
        return actions.length;
    }

    /**
     * Get object for given position
     */
    @Override
    public Object getItem(int position) {
        return null;
    }

    /**
     * Get item id for given position
     */
    @Override
    public long getItemId(int position) {
        return actions[position];
    }

    /**
     * Get view for given position
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.actionitem, null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.label);
        ImageView iconView = (ImageView) convertView.findViewById(R.id.icon);

        textView.setText(labels[position]);
        iconView.setImageResource(icons[position]);

        return convertView;
    }
}
