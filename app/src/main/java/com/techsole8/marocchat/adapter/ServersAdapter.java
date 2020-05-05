
package com.techsole8.marocchat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.techsole8.marocchat.R;
import com.techsole8.marocchat.Yaaic;
import com.techsole8.marocchat.menu.ServerPopupMenu;
import com.techsole8.marocchat.model.Server;

import java.util.List;


public class ServersAdapter extends RecyclerView.Adapter<ServersAdapter.ViewHolder> {
    public interface ClickListener {
        void onServerSelected(Server server);
        void onConnectToServer(Server server);
        void onDisconnectFromServer(Server server);
        void onEditServer(Server server);
        void onDeleteServer(Server server);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView titleView;
        public final TextView hostView;
        public final ImageView connectionView;
        public final View menuView;
        public final ServerPopupMenu popupMenu;



        public ViewHolder(View view, ClickListener listener) {
            super(view);

            titleView = (TextView) view.findViewById(R.id.title);
            hostView = (TextView) view.findViewById(R.id.host);
            connectionView = (ImageView) view.findViewById(R.id.connection);
            menuView = view.findViewById(R.id.menu);

            popupMenu = new ServerPopupMenu(
                view.getContext(), view.findViewById(R.id.menu),
                listener
            );
        }
    }

    private List<Server> servers;
    private ClickListener listener;

    public ServersAdapter(ClickListener listener) {
        this.listener = listener;

        loadServers();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_server, parent, false);

        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Server server = servers.get(position);

        int colorResource = server.isConnected() ? R.color.connected : R.color.disconnected;
        int color = holder.itemView.getContext().getResources().getColor(colorResource);

        holder.titleView.setText(server.getTitle());
        holder.titleView.setTextColor(color);
        holder.connectionView.setImageResource(
                server.isConnected()
                ? R.drawable.ic_navigation_server_connected
                : R.drawable.ic_navigation_server_disconnected
        );
        holder.hostView.setText(String.format("%s @ %s : %d",
                server.getIdentity().getNickname(),
                server.getHost(),
                server.getPort()
        ));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onServerSelected(server);
            }
        });

        holder.popupMenu.updateServer(server);
    }

    public void loadServers() {
        this.servers = Yaaic.getInstance().getServers();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return servers.size();
    }
}
