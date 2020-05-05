/*
Yaaic - Yet Another Android IRC Client

Copyright 2009-2015 Sebastian Kaspari

This file is part of Yaaic.

Yaaic is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Yaaic is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Yaaic.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.techsole8.marocchat.activity;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.techsole8.marocchat.R;


/**
 * "About" dialog activity.
 */
public class AboutActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.about);

        initializeVersionView();
        initializeIrcView();
    }

    private void initializeVersionView() {
        try {
            TextView versionView = (TextView) findViewById(R.id.version);
            versionView.setText(
                getPackageManager().getPackageInfo(getPackageName(), 0).versionName
            );
        } catch (PackageManager.NameNotFoundException e) {
            throw new AssertionError("Should not happen: Can't read application info of myself");
        }
    }

    private void initializeIrcView() {
        TextView ircLinkView = (TextView) findViewById(R.id.about_irclink);
//        ircLinkView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(AboutActivity.this, Register.class);
//                intent.setData(Uri.parse(getString(R.string.app_irc)));
//                startActivity(intent);
//            }
//        });
    }
}
