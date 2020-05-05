
package com.techsole8.marocchat.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.techsole8.marocchat.listener.ServerListener;
import com.techsole8.marocchat.receiver.ServerReceiver;

import com.techsole8.marocchat.R;
import com.techsole8.marocchat.Yaaic;
import com.techsole8.marocchat.activity.AddServerActivity;
import com.techsole8.marocchat.activity.YaaicActivity;
import com.techsole8.marocchat.adapter.ServersAdapter;
import com.techsole8.marocchat.db.Database;
import com.techsole8.marocchat.irc.IRCBinder;
import com.techsole8.marocchat.model.Broadcast;
import com.techsole8.marocchat.model.Extra;
import com.techsole8.marocchat.model.Server;
import com.techsole8.marocchat.model.Status;


public class OverviewFragment extends Fragment implements ServerListener, ServersAdapter.ClickListener, View.OnClickListener {
    public static final String TRANSACTION_TAG = "fragment_overview";

    private ServersAdapter adapter;
    private YaaicActivity activity;
    private BroadcastReceiver receiver;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(context instanceof YaaicActivity)) {
            throw new IllegalArgumentException("Activity has to implement YaaicActivity interface");
        }

        this.activity = (YaaicActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_servers, container, false);

        adapter = new ServersAdapter(this);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

//        ImageButton button = (ImageButton) view.findViewById(R.id.fab);
//        button.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        activity.setToolbarTitle(getString(R.string.app_name));

        receiver = new ServerReceiver(this);
        getActivity().registerReceiver(receiver, new IntentFilter(Broadcast.SERVER_UPDATE));

        adapter.loadServers();
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onClick(View view) {
        final Context context = view.getContext();

        Intent intent = new Intent(context, AddServerActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onServerSelected(Server server) {
        activity.onServerSelected(server);
    }

    @Override
    public void onConnectToServer(Server server) {
        IRCBinder binder = activity.getBinder();

        if (binder != null && server.getStatus() == Status.DISCONNECTED) {
            binder.connect(server);
            server.setStatus(Status.CONNECTING);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDisconnectFromServer(Server server) {
        IRCBinder binder = activity.getBinder();

        if (binder != null) {
            server.clearConversations();
            server.setStatus(Status.DISCONNECTED);
            server.setMayReconnect(false);
            binder.getService().getConnection(server.getId()).quitServer();
        }
    }

    @Override
    public void onEditServer(Server server) {
        if (server.getStatus() != Status.DISCONNECTED) {
            Toast.makeText(getActivity(), getResources().getString(R.string.disconnect_before_editing), Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getActivity(), AddServerActivity.class);
            intent.putExtra(Extra.SERVER, server.getId());
            startActivityForResult(intent, 0);
        }

    }

    @Override
    public void onDeleteServer(Server server) {
        IRCBinder binder = activity.getBinder();

        if (binder != null) {
            binder.getService().getConnection(server.getId()).quitServer();

            Database db = new Database(getActivity());
            db.removeServerById(server.getId());
            db.close();

            Yaaic.getInstance().removeServerById(server.getId());

            getActivity().sendBroadcast(
                    Broadcast.createServerIntent(Broadcast.SERVER_UPDATE, server.getId())
            );
        }
    }

    @Override
    public void onStatusUpdate() {
        adapter.loadServers();
    }
}
