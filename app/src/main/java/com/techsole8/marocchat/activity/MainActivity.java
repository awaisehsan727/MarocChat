
package com.techsole8.marocchat.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techsole8.marocchat.fragment.ConversationFragment;
import com.techsole8.marocchat.fragment.SettingsFragment;
import com.techsole8.marocchat.listener.ServerListener;
import com.techsole8.marocchat.receiver.ServerReceiver;

import com.techsole8.marocchat.R;
import com.techsole8.marocchat.Yaaic;
import com.techsole8.marocchat.db.Database;
import com.techsole8.marocchat.irc.IRCBinder;
import com.techsole8.marocchat.irc.IRCService;
import com.techsole8.marocchat.model.Authentication;
import com.techsole8.marocchat.model.Broadcast;
import com.techsole8.marocchat.model.Extra;
import com.techsole8.marocchat.model.Identity;
import com.techsole8.marocchat.model.Server;
import com.techsole8.marocchat.model.Status;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements YaaicActivity, ServiceConnection, ServerListener {
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    public DrawerLayout drawer;
    private IRCBinder binder;
    private ServerReceiver receiver;
    private LinearLayout serverContainer;
    private Authentication authentication;
    private ArrayList<String> aliases;
    private ArrayList<String> channels;
    private ArrayList<String> commands;
    List<Server> servers;
    String nickname;
    private YaaicActivity activity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initializeToolbar();
        initializeDrawer();
        authentication = new Authentication();
        aliases = new ArrayList<String>();
        channels = new ArrayList<String>();
        channels.add("#quiz");
        channels.add("#maroc");



        commands = new ArrayList<String>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        nickname = preferences.getString("username", "");

//        if (savedInstanceState == null) {
//            onOverview(null);
//        }
        this.servers = Yaaic.getInstance().getServers();
        if (servers.size() > 0) {
            Server server = servers.get(0);
            String host = server.getHost();
            if (!host.equals("irc.marocchat.nl" )) {
                server.isDisconnected();
                Database db =new Database(this);
                deleteDatabase(db.getDatabaseName());
                server.clearConversations();
                addServer();
            }
            else
            {
                if(server.isConnected())
                {

                    onServerSelected(server);
                }
                else
                {
                    server.isDisconnected();
                    Database db = new Database(this);
                    deleteDatabase(db.getDatabaseName());
                    server.clearConversations();
                    addServer();
                }
            }
        }
        else
        {
            Server server=new Server();
            server.isDisconnected();
            Database db =new Database(this);
            deleteDatabase(db.getDatabaseName());
            server.clearConversations();
            addServer();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Server server = servers.get(0);
//        server.setStatus(Status.DISCONNECTED);
//        server.setMayReconnect(false);
  //      binder.getService().getConnection(server.getId()).quitServer();
//        server.clearConversations();
    }

    private void addServer()
    {
        Database db = new Database(this);

        Identity identity = new Identity();
        identity.setNickname(nickname);
        identity.setIdent(nickname);
        identity.setRealName("Maroc Chat App");
        identity.setAliases(aliases);
//        long identityId = db.addIdentity(
//            identity.getNickname(),
//            identity.getIdent(),
//            identity.getRealName(),
//            identity.getAliases()
//            );

        long identityId = db.addIdentity(nickname,nickname,"Maroc Chat App",aliases);
//        db.addIdentity("faizi","ChatApp","Techsole",aliases);
//        db.addIdentity("Nomi","ChatApp","Techsole",aliases);

//        Server server = getServerFromView();
        Server server = new Server();
        server.setHost("irc.marocchat.nl");
        server.setPort(6667);
        server.setPassword("");
        server.setTitle("Status");
        server.setCharset("UTF-8");
        server.setUseSSL(false);
        server.setStatus(Status.DISCONNECTED);

        server.setAuthentication(authentication);
        long serverId = db.addServer(server, (int) identityId);
        db.setChannels((int) serverId, channels);
        db.setCommands((int) serverId, commands);
        db.close();

        server.setId((int) serverId);
        server.setIdentity(identity);
        server.setAutoJoinChannels(channels);
        server.setConnectCommands(commands);

        Yaaic.getInstance().addServer(server);
        onServerSelected(server);
    }

    public void initializeToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

   public void initializeDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);

        drawer.setDrawerListener(toggle);

        serverContainer = (LinearLayout) findViewById(R.id.server_container);


  }

    public void updateDrawerServerList() {
        List<Server> servers = Yaaic.getInstance().getServers();
       // drawerEmptyView.setVisibility(servers.size() > 0 ? View.GONE : View.VISIBLE);

        serverContainer.removeAllViews();

        for (final Server server : servers) {
            TextView serverView = (TextView) getLayoutInflater().inflate(R.layout.item_drawer_server, drawer, false);
            serverView.setText(server.getTitle());

            serverView.setCompoundDrawablesWithIntrinsicBounds(
                    getDrawable(server.isConnected()
                        ? R.drawable.ic_navigation_server_connected
                        : R.drawable.ic_navigation_server_disconnected),
                    null,
                    null,
                    null
            );

            int colorResource = server.isConnected() ? R.color.connected : R.color.disconnected;
            serverView.setTextColor(ContextCompat.getColor(this, colorResource));

            serverView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onServerSelected(server);

                    drawer.closeDrawers();
                }
            });

            serverContainer.addView(serverView, 0);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        toggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();

        receiver = new ServerReceiver(this);
        registerReceiver(receiver, new IntentFilter(Broadcast.SERVER_UPDATE));

        Intent intent = new Intent(this, IRCService.class);
        intent.setAction(IRCService.ACTION_BACKGROUND);
        startService(intent);

        bindService(intent, this, 0);

        updateDrawerServerList();
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(receiver);

        if (binder != null && binder.getService() != null) {
            binder.getService().checkServiceStatus();
        }

        unbindService(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        return false;
    }

    @Override
    public void onServerSelected(Server server) {
        Bundle arguments = new Bundle();

        if (server.getStatus() == Status.DISCONNECTED && !server.mayReconnect()) {
            server.setStatus(Status.PRE_CONNECTING);

            arguments.putBoolean(Extra.CONNECT, true);
        }

        arguments.putInt(Extra.SERVER_ID, server.getId());

        ConversationFragment fragment = new ConversationFragment();
        fragment.setArguments(arguments);

        switchToFragment(fragment, ConversationFragment.TRANSACTION_TAG + "-" + server.getId());
    }

   // public void onOverview(View view) {
       // switchToFragment(new OverviewFragment(), OverviewFragment.TRANSACTION_TAG);
   // }

    public void onSettings(View view)
    {
        drawer.closeDrawers();
        Intent intent = new Intent(MainActivity.this, Setting.class);
       startActivity(intent);
    }

    private void switchToFragment(Fragment fragment, String tag) {
        drawer.closeDrawers();

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.findFragmentByTag(tag) != null) {
            // We are already showing this fragment
            return;
        }

        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                .replace(R.id.container, fragment, tag)
                .commit();
    }

    public void onAbout(View view) {
        drawer.closeDrawers();
        startActivity(new Intent(this, AboutActivity.class));
    }

    @Override
    public IRCBinder getBinder() {
        return binder;
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public void setToolbarTitle(String title) {
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        binder = (IRCBinder) service;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        binder = null;
    }

    @Override
    public void onStatusUpdate() {
        updateDrawerServerList();
    }


}
