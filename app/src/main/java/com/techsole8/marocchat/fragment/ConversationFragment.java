
package com.techsole8.marocchat.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.techsole8.marocchat.listener.ConversationListener;
import com.techsole8.marocchat.listener.ServerListener;
import com.techsole8.marocchat.receiver.ConversationReceiver;
import com.techsole8.marocchat.receiver.ServerReceiver;

import com.techsole8.marocchat.R;
import com.techsole8.marocchat.Yaaic;
import com.techsole8.marocchat.activity.GetGuest;
import com.techsole8.marocchat.activity.UserActivity;
import com.techsole8.marocchat.activity.UsersActivity;
import com.techsole8.marocchat.activity.YaaicActivity;
import com.techsole8.marocchat.adapter.ConversationPagerAdapter;
import com.techsole8.marocchat.adapter.MessageListAdapter;
import com.techsole8.marocchat.command.CommandParser;
import com.techsole8.marocchat.irc.IRCBinder;
import com.techsole8.marocchat.irc.IRCConnection;
import com.techsole8.marocchat.irc.IRCService;
import com.techsole8.marocchat.model.Broadcast;
import com.techsole8.marocchat.model.Conversation;
import com.techsole8.marocchat.model.Extra;
import com.techsole8.marocchat.model.Message;
import com.techsole8.marocchat.model.Query;
import com.techsole8.marocchat.model.Scrollback;
import com.techsole8.marocchat.model.Server;
import com.techsole8.marocchat.model.ServerInfo;
import com.techsole8.marocchat.model.Settings;
import com.techsole8.marocchat.model.Status;
import com.techsole8.marocchat.model.User;
import com.techsole8.marocchat.view.ConversationTabLayout;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ConversationFragment extends Fragment implements ServerListener, ConversationListener, ServiceConnection
         {
    public static final String TRANSACTION_TAG = "fragment_conversation";

    private static final int REQUEST_CODE_JOIN = 1;
    private static final int REQUEST_CODE_USERS = 2;
    private static final int REQUEST_CODE_USER = 3;
    private static final int REQUEST_CODE_NICK_COMPLETION= 4;

    private int serverId;
    private Server server;
    private IRCBinder binder;
    private ConversationReceiver channelReceiver;
    private ServerReceiver serverReceiver;

    private YaaicActivity activity;

    private EditText input;
    private ViewPager pager;
    private ConversationPagerAdapter pagerAdapter;
    private ConversationTabLayout tabLayout;
    private int index;
    private Menu localmenu;
    public Boolean close = false;
    String channel;


    private Scrollback scrollback;

    // XXX: This is ugly. This is a buffer for a channel that should be joined after showing the
    //      JoinActivity. As onActivityResult() is called before onResume() a "channel joined"
    //      broadcast may get lost as the broadcast receivers are registered in onResume() but the
    //      join command would be called in onActivityResult(). joinChannelBuffer will save the
    //      channel name in onActivityResult() and run the join command in onResume().
    private String joinChannelBuffer;

    private Snackbar snackbar;



    private final View.OnKeyListener inputKeyListener = new View.OnKeyListener() {
        /**
         * On key pressed (input line)
         */
        @Override
        public boolean onKey(View view, int keyCode, KeyEvent event) {
            EditText input = (EditText) view;

            if (event.getAction() != KeyEvent.ACTION_DOWN) {
                return false;
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                String message = scrollback.goBack();
                if (message != null) {
                    input.setText(message);
                }
                return true;
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                String message = scrollback.goForward();
                if (message != null) {
                    input.setText(message);
                }
                return true;
            }

            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                sendCurrentMessage();

                return true;
            }

            // Nick completion
            if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                doNickCompletion(input);
                return true;
            }

            return false;
        }
    };

    public ConversationFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(context instanceof YaaicActivity)) {
            throw new IllegalArgumentException("Activity has to implement YaaicActivity interface");
        }

        this.activity = (YaaicActivity) context;
    }

    /**
     * On create
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        serverId = getArguments().getInt("serverId");
        server = Yaaic.getInstance().getServerById(serverId);

        scrollback = new Scrollback();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversations, container, false);

        Settings settings = new Settings(getActivity());

        boolean isLandscape = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);

        input = (EditText) view.findViewById(R.id.input);
        input.setOnKeyListener(inputKeyListener);

        pager = (ViewPager) view.findViewById(R.id.pager);

        pagerAdapter = new ConversationPagerAdapter(getActivity(), server);
        pager.setAdapter(pagerAdapter);

        tabLayout = new ConversationTabLayout(container.getContext());
        tabLayout.setViewPager(pager);
        tabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        tabLayout.setDividerColors(getResources().getColor(R.color.divider));
        tabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("Awais", "page position"+position);
            //to inflate menu at the ttolbar like Users,Logout and Close
                String page=pagerAdapter.getPageTitle(position);


                if (page.contains("#"))
                {
                    activity.getToolbar().getMenu().clear();
                    activity.getToolbar().inflateMenu(R.menu.conversations);

                }
                else if(page.equals("Status"))
                {
                    activity.getToolbar().getMenu().clear();
                    activity.getToolbar().inflateMenu(R.menu.conversations);

                }
                else if( page.equals("marocchat"))
                {
                    activity.getToolbar().getMenu().clear();
                    activity.getToolbar().inflateMenu(R.menu.conversations);
                }
                else
                {
                    activity.getToolbar().getMenu().clear();
                    activity.getToolbar().inflateMenu(R.menu.close);

                }

            }

            @Override
            public void onPageSelected(int position) {
                index = position;
                channel= pagerAdapter.getPageTitle(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        Toolbar.LayoutParams params = new Toolbar.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        );
        params.gravity = Gravity.BOTTOM;


        activity.getToolbar().addView(tabLayout, params);

        if (server.getStatus() == Status.PRE_CONNECTING) {
            server.clearConversations();
            pagerAdapter.clearConversations();
            server.getConversation(ServerInfo.DEFAULT_NAME).setHistorySize(
                settings.getHistorySize()
            );
        }

        // Optimization : cache field lookups
        Collection<Conversation> mConversations = server.getConversations();

        for (Conversation conversation : mConversations) {
            // Only scroll to new conversation if it was selected before
            if (conversation.getStatus() == Conversation.STATUS_SELECTED) {
                onNewConversation(conversation.getName());
            } else {
                createNewConversation(conversation.getName());
            }
        }

        int setInputTypeFlags = 0;

        setInputTypeFlags |= InputType.TYPE_TEXT_FLAG_AUTO_CORRECT;

        if (settings.autoCapSentences()) {
            setInputTypeFlags |= InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
        }

        if (isLandscape && settings.imeExtract()) {
            setInputTypeFlags |= InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE;
        }

        if (!settings.imeExtract()) {
            input.setImeOptions(input.getImeOptions() | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        }

        input.setInputType(input.getInputType() | setInputTypeFlags);

        ImageButton sendButton = (ImageButton) view.findViewById(R.id.send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input.getText().length() > 0) {
                    sendCurrentMessage();
                }
            }
        });

        sendButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                doNickCompletion(input);
                return true;
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        activity.getToolbar().removeView(tabLayout);
    }

    /**
     * On resume
     */
    @Override
    public void onResume() {
        // register the receivers as early as possible, otherwise we may loose a broadcast message
        channelReceiver = new ConversationReceiver(server.getId(), this);
        getActivity().registerReceiver(channelReceiver, new IntentFilter(Broadcast.CONVERSATION_MESSAGE));
        getActivity().registerReceiver(channelReceiver, new IntentFilter(Broadcast.CONVERSATION_NEW));
        getActivity().registerReceiver(channelReceiver, new IntentFilter(Broadcast.CONVERSATION_REMOVE));
        getActivity().registerReceiver(channelReceiver, new IntentFilter(Broadcast.CONVERSATION_TOPIC));

        serverReceiver = new ServerReceiver(this);
        getActivity().registerReceiver(serverReceiver, new IntentFilter(Broadcast.SERVER_UPDATE));

        super.onResume();

        // Start service
        Intent intent = new Intent(getActivity(), IRCService.class);
        intent.setAction(IRCService.ACTION_FOREGROUND);
        getActivity().startService(intent);
        getActivity().bindService(intent, this, 0);

        input.setEnabled(server.isConnected());

        // Optimization - cache field lookup
        Collection<Conversation> mConversations = server.getConversations();
        MessageListAdapter mAdapter;

        // Fill view with messages that have been buffered while paused
        for (Conversation conversation : mConversations) {
            String name = conversation.getName();
            mAdapter = pagerAdapter.getItemAdapter(name);

            if (mAdapter != null) {
                mAdapter.addBulkMessages(conversation.getBuffer());
                conversation.clearBuffer();
            } else {
                // Was conversation created while we were paused?
                if (pagerAdapter.getPositionByName(name) == -1) {
                    onNewConversation(name);
                }
            }

            // Clear new message notifications for the selected conversation
            if (conversation.getStatus() == Conversation.STATUS_SELECTED && conversation.getNewMentions() > 0) {
                Intent ackIntent = new Intent(getActivity(), IRCService.class);
                ackIntent.setAction(IRCService.ACTION_ACK_NEW_MENTIONS);
                ackIntent.putExtra(IRCService.EXTRA_ACK_SERVERID, serverId);
                ackIntent.putExtra(IRCService.EXTRA_ACK_CONVTITLE, name);
                getActivity().startService(ackIntent);
            }
        }

        // Remove views for conversations that ended while we were paused
        int numViews = pagerAdapter.getCount();
        if (numViews > mConversations.size()) {
            for (int i = 0; i < numViews; ++i) {
                if (!mConversations.contains(pagerAdapter.getItem(i))) {
                    pagerAdapter.removeConversation(i--);
                    --numViews;
                }
            }
        }

        // Join channel that has been selected in JoinActivity (onActivityResult())
        if (joinChannelBuffer != null) {
            new Thread() {
                @Override
                public void run() {
                    binder.getService().getConnection(serverId).joinChannel(joinChannelBuffer);
                    joinChannelBuffer = null;
                }
            }.start();
        }

        server.setIsForeground(true);
    }

    /**
     * On Pause
     */
    @Override
    public void onPause() {
        super.onPause();

        server.setIsForeground(false);

        if (binder != null && binder.getService() != null) {
            binder.getService().checkServiceStatus();
        }

        getActivity().unbindService(this);
        getActivity().unregisterReceiver(channelReceiver);
        getActivity().unregisterReceiver(serverReceiver);
    }

    /**
     * On service connected
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.binder = (IRCBinder) service;

        // connect to irc server if connect has been requested
        if (server.getStatus() == Status.PRE_CONNECTING && getArguments().containsKey("connect")) {
            server.setStatus(Status.CONNECTING);
            binder.connect(server);
        } else {
            onStatusUpdate();
        }
    }

    /**
     * On service disconnected
     */
    @Override
    public void onServiceDisconnected(ComponentName name) {
        this.binder = null;
    }
    /**
     * On options menu requested
     */
    @SuppressLint("RestrictedApi")
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

//        if ( index < 4){
//            inflater.inflate(R.menu.conversations, menu);
//            Drawable yourdrawable = menu.getItem(0).getIcon(); // change 0 with 1,2 ...
//            yourdrawable.mutate();
//            yourdrawable.setColorFilter(getResources().getColor(R.color.Red), PorterDuff.Mode.SRC_IN);
//
//        }else {
//            inflater.inflate(R.menu.close, menu);
//
//        }




        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        Conversation conversation = pagerAdapter.getItem(pager.getCurrentItem());
     //   menu.findItem(R.id.notify).setChecked(conversation.shouldAlwaysNotify());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.disconnect:
                server.setStatus(Status.DISCONNECTED);
                server.setMayReconnect(false);
                binder.getService().getConnection(serverId).quitServer();
                server.clearConversations();
                Intent intents = new Intent(getActivity(), GetGuest.class);
                intents.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intents, REQUEST_CODE_USERS);
                getActivity().finish();
                
                break;

            case R.id.clo:
                Conversation conversationToClose = pagerAdapter.getItem(pager.getCurrentItem());
                // Make sure we part a channel when closing the channel conversation
                if (conversationToClose.getType() == Conversation.TYPE_CHANNEL) {
                    binder.getService().getConnection(serverId).partChannel(conversationToClose.getName());
                }
                else if (conversationToClose.getType() == Conversation.TYPE_QUERY) {
                    server.removeConversation(conversationToClose.getName());
                    onRemoveConversation(conversationToClose.getName());
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.close_server_window), Toast.LENGTH_SHORT).show();
                }
                break;

//            case R.id.join:
//                startActivityForResult(new Intent(getActivity(), JoinActivity.class), REQUEST_CODE_JOIN);
//                break;

            case R.id.users:
                Conversation conversationForUserList = pagerAdapter.getItem(pager.getCurrentItem());
                if (conversationForUserList.getType() == Conversation.TYPE_CHANNEL) {
                    Intent intent = new Intent(getActivity(), UsersActivity.class);
                    intent.putExtra(
                            Extra.USERS,
                            binder.getService().getConnection(server.getId()).getUsersAsStringArray(
                                    conversationForUserList.getName()
                            )
                    );
                    startActivityForResult(intent, REQUEST_CODE_USERS);
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.only_usable_from_channel), Toast.LENGTH_SHORT).show();
                }
                break;

//            case R.id.notify:
//                Conversation conversationForNotify = pagerAdapter.getItem(pager.getCurrentItem());
//                conversationForNotify.setAlwaysNotify(!item.isChecked());
//                break;
        }

        return true;
    }

    /**
     * Get server object assigned to this activity
     *
     * @return the server object
     */
    public Server getServer() {
        return server;
    }

    /**
     * On conversation message
     */
    @Override
    public void onConversationMessage(String target) {
        Conversation conversation = server.getConversation(target);

        if (conversation == null) {
            // In an early state it can happen that the conversation object
            // is not created yet.
            return;
        }

        MessageListAdapter adapter = pagerAdapter.getItemAdapter(target);

        while(conversation.hasBufferedMessages()) {
            Message message = conversation.pollBufferedMessage();

            if (adapter != null && message != null) {
                adapter.addMessage(message);
                int status;

                switch (message.getType())
                {
                    case Message.TYPE_MISC:
                        status = Conversation.STATUS_MISC;
                        break;

                    default:
                        status = Conversation.STATUS_MESSAGE;
                        break;
                }
                conversation.setStatus(status);
            }
        }
    }

    /**
     * On new conversation
     */
    @Override
    public void onNewConversation(String target) {
        createNewConversation(target);

        pager.setCurrentItem(pagerAdapter.getCount() - 1);
    }

    /**
     * Create a new conversation in the pager adapter for the
     * given target conversation.
     *
     * @param target
     */
    public void createNewConversation(String target) {
        pagerAdapter.addConversation(server.getConversation(target));

        tabLayout.update();
    }

    /**
     * On conversation remove
     */
    @Override
    public void onRemoveConversation(String target) {
        int position = pagerAdapter.getPositionByName(target);

        if (position != -1) {
            pagerAdapter.removeConversation(position);
        }

        tabLayout.update();
    }

    /**
     * On topic change
     */
    @Override
    public void onTopicChanged(String target) {
        // No implementation
    }

    /**
     * On server status update
     */
    @Override
    public void onStatusUpdate() {
        if (server.isConnected()) {
            input.setEnabled(true);
        } else {
            input.setEnabled(false);

            if (server.getStatus() == Status.CONNECTING) {
                return;
            }

            // Service is not connected or initialized yet - See #54
            if (binder == null || binder.getService() == null || binder.getService().getSettings() == null) {
                return;
            }

            if (!binder.getService().getSettings().isReconnectEnabled()) {
                if (snackbar == null) {
                    snackbar = Snackbar.make(pager,
                            getString(R.string.disconnect_info, server.getTitle()),
                            Snackbar.LENGTH_INDEFINITE
                    );

                    snackbar.setAction(R.string.action_reconnect, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!server.isDisconnected()) {
                                return;
                            }

                            binder.getService().getConnection(server.getId()).setAutojoinChannels(
                                    server.getCurrentChannelNames()
                            );
                            server.setStatus(Status.CONNECTING);
                            binder.connect(server);
                        }
                    });
                }

                if (!snackbar.isShown()) {
                    snackbar.show();
                }
            }
        }
    }

    /**
     * On activity result
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {



        if (resultCode != Activity.RESULT_OK) {
            // ignore other result codes
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_JOIN:
                joinChannelBuffer = data.getExtras().getString("channel");
                break;
            case REQUEST_CODE_USERS:
                Intent intent = new Intent(getActivity(), UserActivity.class);
                intent.putExtra(Extra.USER, data.getStringExtra(Extra.USER));
                startActivityForResult(intent, REQUEST_CODE_USER);
                break;
            case REQUEST_CODE_NICK_COMPLETION:
                insertNickCompletion(input, data.getExtras().getString(Extra.USER));
                break;
            case REQUEST_CODE_USER:
                final int actionId = data.getExtras().getInt(Extra.ACTION);
                final String nickname = data.getExtras().getString(Extra.USER);
                final IRCConnection connection = binder.getService().getConnection(server.getId());
             //   final ArrayList<String> conversation = server.getCurrentChannelNames();
                final Handler handler = new Handler();

                // XXX: Implement me - The action should be handled after onResume()
                //                     to catch the broadcasts... now we just wait a second
                // Yes .. that's very ugly - we need some kind of queue that is handled after onResume()

                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // Do nothing
                        }

                        String nicknameWithoutPrefix = nickname;

                        while (
                                nicknameWithoutPrefix.startsWith("@") ||
                                        nicknameWithoutPrefix.startsWith("+") ||
                                        nicknameWithoutPrefix.startsWith(".") ||
                                        nicknameWithoutPrefix.startsWith("%")
                                ) {
                            // Strip prefix(es) now
                            nicknameWithoutPrefix = nicknameWithoutPrefix.substring(1);
                        }

                        switch (actionId) {
                            case User.ACTION_REPLY:
                                final String replyText = nicknameWithoutPrefix + ": ";
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        input.setText(replyText);
                                        input.setSelection(replyText.length());
                                    }
                                });
                                break;
                            case User.ACTION_QUERY:
                                Conversation query = server.getConversation(nicknameWithoutPrefix);
                                if (query == null) {
                                    // Open a query if there's none yet
                                    query = new Query(nicknameWithoutPrefix);
                                    query.setHistorySize(binder.getService().getSettings().getHistorySize());
                                    server.addConversation(query);



                                    Intent intent = Broadcast.createConversationIntent(
                                            Broadcast.CONVERSATION_NEW,
                                            server.getId(),
                                            nicknameWithoutPrefix
                                    );
                                    binder.getService().sendBroadcast(intent);



                                }
                                break;
                            case User.ACTION_Whois:
                                connection.Whois(channel, nicknameWithoutPrefix);
                                break;
                            case User.ACTION_OP:
                                connection.op(channel, nicknameWithoutPrefix);
                                break;
                            case User.ACTION_DEOP:
                                connection.deOp(channel, nicknameWithoutPrefix);
                                break;
                            case User.ACTION_VOICE:
                                connection.voice(channel, nicknameWithoutPrefix);
                                break;
                            case User.ACTION_DEVOICE:
                                connection.deVoice(channel, nicknameWithoutPrefix);
                                break;
                            case User.ACTION_KICK:
                                connection.kick(channel, nicknameWithoutPrefix);
                                break;
                            case User.ACTION_IGNORE:
                                connection.ignore(channel, nicknameWithoutPrefix);
                                break;
                            case User.ACTION_UNIGNORE:
                                connection.unignore(channel, nicknameWithoutPrefix);
                                break;
                            case User.ACTION_BAN:
                                connection.ban(channel, nicknameWithoutPrefix + "!*@*");
                                break;

                        }
                    }
                }.start();

                break;
        }
    }


    private void sendCurrentMessage() {
        sendMessage(input.getText().toString());

        // Workaround for a race condition in EditText
        // Instead of calling input.setText("");
        // See:
        // - https://github.com/pocmo/Yaaic/issues/67
        // - http://code.google.com/p/android/issues/detail?id=17508
        TextKeyListener.clear(input.getText());
    }

    /**
     * Send a message in this conversation
     *
     * @param text The text of the message
     */
    private void sendMessage(String text) {
        if (text.equals("")) {
            // ignore empty messages
            return;
        }

        if (!server.isConnected()) {
            Message message = new Message(getString(R.string.message_not_connected));
            message.setColor(R.color.accent);
            message.setIcon(R.drawable.error);
            server.getConversation(server.getSelectedConversation()).addMessage(message);
            onConversationMessage(server.getSelectedConversation());
        }

        scrollback.addMessage(text);

        Conversation conversation = pagerAdapter.getItem(pager.getCurrentItem());

        if (conversation != null) {
            if (!text.trim().startsWith("/")) {
                if (conversation.getType() != Conversation.TYPE_SERVER) {
                    String nickname = binder.getService().getConnection(serverId).getNick();
                    //conversation.addMessage(new Message("<" + nickname + "> " + text));
                    conversation.addMessage(new Message(text, nickname));
                    binder.getService().getConnection(serverId).sendMessage(conversation.getName(), text);
                } else {
                    Message message = new Message(getString(R.string.chat_only_form_channel));
                    message.setColor(R.color.Yellow);
                    message.setIcon(R.drawable.warning);
                    conversation.addMessage(message);
                }
                onConversationMessage(conversation.getName());
            } else {
                CommandParser.getInstance().parse(text, server, conversation, binder.getService());
            }
        }
    }

    /**
     * Complete a nick in the input line
     */
    private void doNickCompletion(EditText input) {
        String text = input.getText().toString();

        if (text.length() <= 0) {
            return;
        }

        String[] tokens = text.split("[\\s,.-]+");

        if (tokens.length <= 0) {
            return;
        }

        String word = tokens[tokens.length - 1].toLowerCase();
        tokens[tokens.length - 1] = null;

        int begin   = input.getSelectionStart();
        int end     = input.getSelectionEnd();
        int cursor  = Math.min(begin, end);
        int sel_end = Math.max(begin, end);

        boolean in_selection = (cursor != sel_end);

        if (in_selection) {
            word = text.substring(cursor, sel_end);
        } else {
            // use the word at the curent cursor position
            while (true) {
                cursor -= 1;
                if (cursor <= 0 || text.charAt(cursor) == ' ') {
                    break;
                }
            }

            if (cursor < 0) {
                cursor = 0;
            }

            if (text.charAt(cursor) == ' ') {
                cursor += 1;
            }

            sel_end = text.indexOf(' ', cursor);

            if (sel_end == -1) {
                sel_end = text.length();
            }

            word = text.substring(cursor, sel_end);
        }
        // Log.d("Yaaic", "Trying to complete nick: " + word);

        Conversation conversationForUserList = pagerAdapter.getItem(pager.getCurrentItem());

        String[] users = null;

        if (conversationForUserList.getType() == Conversation.TYPE_CHANNEL) {
            users = binder.getService().getConnection(server.getId()).getUsersAsStringArray(
                    conversationForUserList.getName()
            );
        }

        // go through users and add matches
        if (users != null) {
            List<Integer> result = new ArrayList<Integer>();

            for (int i = 0; i < users.length; i++) {
                String nick = removeStatusChar(users[i].toLowerCase());
                if (nick.startsWith(word.toLowerCase())) {
                    result.add(Integer.valueOf(i));
                }
            }

            if (result.size() == 1) {
                input.setSelection(cursor, sel_end);
                insertNickCompletion(input, users[result.get(0).intValue()]);
            } else if (result.size() > 0) {
                Intent intent  = new Intent(getActivity(), UsersActivity.class);
                String[] extra = new String[result.size()];
                int i = 0;

                for (Integer n : result) {
                    extra[i++] = users[n.intValue()];
                }

                input.setSelection(cursor, sel_end);
                intent.putExtra(Extra.USERS, extra);
                startActivityForResult(intent, REQUEST_CODE_NICK_COMPLETION);
            }
        }
    }

    /**
     * Insert a given nick completion into the input line
     *
     * @param input The input line widget, with the incomplete nick selected
     * @param nick The completed nick
     */
    private void insertNickCompletion(final EditText input, String nick) {
        int start = input.getSelectionStart();
        int end  = input.getSelectionEnd();
        nick = removeStatusChar(nick);

        if (start == 0) {
            nick += ":";
        }

        nick += " ";
        input.getText().replace(start, end, nick, 0, nick.length());
        // put cursor after inserted text
        input.setSelection(start + nick.length());
        input.clearComposingText();
        input.post(new Runnable() {
            @Override
            public void run() {
                // make the softkeyboard come up again (only if no hw keyboard is attached)
                openSoftKeyboard(input);
            }
        });

        input.requestFocus();
    }

    /**
     * Open the soft keyboard (helper function)
     */
    private void openSoftKeyboard(View view) {
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * Remove the status char off the front of a nick if one is present
     *
     * @param nick
     * @return nick without statuschar
     */
    private String removeStatusChar(String nick) {
        /* Discard status characters */
        if (nick.startsWith("@") || nick.startsWith("+")
                || nick.startsWith("%")) {
            nick = nick.substring(1);
        }
        return nick;
    }

}
