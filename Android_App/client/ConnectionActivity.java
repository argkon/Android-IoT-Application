package com.example.panos.client;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.panos.parameters.AppParameters;
import com.example.panos.parameters.AppState;



public class ConnectionActivity extends AppCompatActivity {


    Switch modeSwitch;
    Button buttonConnect;
    Button buttonDisconnect;
    TextView modeText;


    private void myLogAndToast(String title, final String content, boolean showtoast) {
        Log.i(title, content);

        if (showtoast) { // Send to main thread a message to show a toast
            if (AppState.controller.getContext() != null) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Context con = AppState.controller.getContext();
                        Toast.makeText(con, content, Toast.LENGTH_LONG).show();
                    }
                }, 0);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);

        modeSwitch = findViewById(R.id.modeswitch);
        buttonConnect = findViewById(R.id.buttonConnect);
        buttonDisconnect = findViewById(R.id.buttonDisconnect);
        modeText = findViewById(R.id.textView5);




        modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (modeSwitch.isChecked()) {
                    modeText.setText("Auto");
                    buttonConnect.setEnabled(false); // Disable Buttons
                    buttonDisconnect.setEnabled(false);

                    buttonConnect.setVisibility(buttonConnect.INVISIBLE);
                    buttonDisconnect.setVisibility(buttonDisconnect.INVISIBLE);
                    AppState.controller.getConnectionMode().setAutomatic(true); // Auto Mode
                } else {
                    modeText.setText("Manual");
                    buttonConnect.setEnabled(true); // Enable and Visible Buttons
                    buttonDisconnect.setEnabled(true);

                    buttonConnect.setVisibility(buttonConnect.VISIBLE);
                    buttonDisconnect.setVisibility(buttonDisconnect.VISIBLE);
                    AppState.controller.getConnectionMode().setAutomatic(false); // Manual Mode
                }
            }
        });

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AppState.controller.subscribed()) {
                    AppState.controller.subscribeToAll(ConnectionActivity.this.getApplicationContext()); // subscribe to MQTT Server
                } else {
                    myLogAndToast("INFO", "Already have MQTT Connection", true);
                }

            }
        });

        buttonDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppState.controller.subscribed()) {
                    String status = AppState.controller.unsubscribeFromAll(); // unsubscribe to MQTT Server
                    if(status.equals("OK")){
                        AppParameters.flagSub = false;
                        AppParameters.toastUpConnection = false;
                    }
                } else {
                    myLogAndToast("INFO", "No MQTT Connection already", true);
                }
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        modeSwitch = findViewById(R.id.modeswitch);

        if (AppState.controller.getConnectionMode().isAutomatic()) {
            modeSwitch.setChecked(true);
        } else {
            modeSwitch.setChecked(false);
        }

        if (modeSwitch.isChecked()) {
            modeText.setText("Auto");
            buttonConnect.setEnabled(false);
            buttonDisconnect.setEnabled(false);
            buttonConnect.setVisibility(buttonConnect.INVISIBLE);
            buttonDisconnect.setVisibility(buttonDisconnect.INVISIBLE);
        } else {
            modeText.setText("Manual");
            buttonConnect.setEnabled(true);
            buttonDisconnect.setEnabled(true);
            buttonConnect.setVisibility(buttonConnect.VISIBLE);
            buttonDisconnect.setVisibility(buttonDisconnect.VISIBLE);
        }
    }
}