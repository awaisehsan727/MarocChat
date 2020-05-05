package com.techsole8.marocchat.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.techsole8.marocchat.R;
import com.techsole8.marocchat.Yaaic;
import com.techsole8.marocchat.model.Server;

import java.util.List;
import java.util.Random;

public class GetGuest extends AppCompatActivity {
    int max;
    int min;
    Toolbar toolbar;
    List<Server> servers;
    String nickname;
    String  empty;
    String rendam;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_guest);
        Button Go=findViewById(R.id.Go);
        final EditText Name=findViewById(R.id.editText);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        nickname = preferences.getString("name", "");
        getRandomNumber();
        Name.setCursorVisible(false);
        Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name.setCursorVisible(true);
            }
        });


        rendam="Gast_"+getRandomNumber();


        Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name.setCursorVisible(true);

            }
        });

        Go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = Name.getText().toString();
                if (name.equals(""))
                {
                    String Guest=rendam.toString();
                    Intent intent = new Intent(GetGuest.this, MainActivity.class);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GetGuest.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("username",Guest);
                    editor.apply();
                    startActivity(intent);
                    finish();
                }
                else
                {
                    String  nick= name;
                    Intent intent = new Intent(GetGuest.this, MainActivity.class);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GetGuest.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("username", nick);
                    editor.apply();
                    startActivity(intent);
                    finish();
                }
            }
        });

        this.servers = Yaaic.getInstance().getServers();
        if (servers.size() > 0) {
            Server server = servers.get(0);
            if (!server.isConnected())
            {
                empty="Nothing";
            }
            else
            {
                if (nickname!=null)
                {
                    Intent intent = new Intent(GetGuest.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }
    }


    private int getRandomNumber() {
        return (new Random()).nextInt((max - min) + 100) + min;
    }

}
