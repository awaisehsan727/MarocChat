
package com.techsole8.marocchat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.techsole8.marocchat.R;
import com.techsole8.marocchat.model.Extra;


public class AuthenticationActivity extends Activity implements OnCheckedChangeListener, OnClickListener
{
    private CheckBox nickservCheckbox;
    private TextView nickservPasswordLabel;
    private EditText nickservPasswordEditText;

    private CheckBox saslCheckbox;
    private TextView saslUsernameLabel;
    private EditText saslUsernameEditText;
    private TextView saslPasswordLabel;
    private EditText saslPasswordEditText;

    /**
     * On create
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.authentication);

        nickservCheckbox = (CheckBox) findViewById(R.id.nickserv_checkbox);
        nickservPasswordLabel = (TextView) findViewById(R.id.nickserv_label_password);
        nickservPasswordEditText = (EditText) findViewById(R.id.nickserv_password);

        saslCheckbox = (CheckBox) findViewById(R.id.sasl_checkbox);
        saslUsernameLabel = (TextView) findViewById(R.id.sasl_label_username);
        saslUsernameEditText = (EditText) findViewById(R.id.sasl_username);
        saslPasswordLabel = (TextView) findViewById(R.id.sasl_label_password);
        saslPasswordEditText = (EditText) findViewById(R.id.sasl_password);

        nickservCheckbox.setOnCheckedChangeListener(this);
        saslCheckbox.setOnCheckedChangeListener(this);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String nickservPassword = extras.getString(Extra.NICKSERV_PASSWORD);

            if (nickservPassword != null && nickservPassword.length() > 0) {
                nickservCheckbox.setChecked(true);
                nickservPasswordEditText.setText(nickservPassword);
            }

            String saslUsername = extras.getString(Extra.SASL_USER);
            String saslPassword = extras.getString(Extra.SASL_PASSWORD);

            if (saslUsername != null && saslUsername.length() > 0) {
                saslCheckbox.setChecked(true);
                saslUsernameEditText.setText(saslUsername);
                saslPasswordEditText.setText(saslPassword);
            }
        }

        ((Button) findViewById(R.id.ok)).setOnClickListener(this);
        ((Button) findViewById(R.id.cancel)).setOnClickListener(this);
    }

    /**
     * On checkbox changed
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        switch (buttonView.getId()) {
            case R.id.nickserv_checkbox:
                nickservPasswordLabel.setEnabled(isChecked);
                nickservPasswordEditText.setEnabled(isChecked);

                if (!isChecked) {
                    nickservPasswordEditText.setText("");
                }

                break;

            case R.id.sasl_checkbox:
                saslUsernameLabel.setEnabled(isChecked);
                saslUsernameEditText.setEnabled(isChecked);
                saslPasswordLabel.setEnabled(isChecked);
                saslPasswordEditText.setEnabled(isChecked);

                if (!isChecked) {
                    saslUsernameEditText.setText("");
                    saslPasswordEditText.setText("");
                }

                break;
        }
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.ok:
                Intent intent = new Intent();
                intent.putExtra(Extra.NICKSERV_PASSWORD, nickservPasswordEditText.getText().toString());
                intent.putExtra(Extra.SASL_USER, saslUsernameEditText.getText().toString());
                intent.putExtra(Extra.SASL_PASSWORD, saslPasswordEditText.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
                break;

            case R.id.cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }
}
