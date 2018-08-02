package com.example.panos.client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.example.panos.clientlisteners.SaveListener;
import com.example.panos.parameters.AppParameters;

public class SettingsActivity extends AppCompatActivity { // Android Settings

    private SaveListener saveListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button buttonSave = findViewById(R.id.buttonSaveSettings);

        saveListener = new SaveListener(this);
        buttonSave.setOnClickListener(saveListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        EditText editIP = findViewById(R.id.editIP);
        EditText editPORT = findViewById(R.id.editPORT);
        EditText editFREQUENCY = findViewById(R.id.editFREQUENCY);

        String ip = AppParameters.ip;
        editIP.setText(ip);

        int port = AppParameters.port;
        editPORT.setText(String.valueOf(port));

        int frequency = AppParameters.frequency;
        editFREQUENCY.setText(String.valueOf(frequency));

    }


    @Override
    protected void onPause() {
        super.onPause();
    }
}