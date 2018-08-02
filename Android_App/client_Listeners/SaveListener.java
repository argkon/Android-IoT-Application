package com.example.panos.clientlisteners;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.panos.client.R;
import com.example.panos.client.SettingsActivity;
import com.example.panos.controllerthreads.Command;
import com.example.panos.controllerthreads.ControllerRunnable;
import com.example.panos.parameters.AppParameters;

import java.util.regex.Pattern;

public class SaveListener implements View.OnClickListener {

    // https://stackoverflow.com/questions/5667371/validate-ipv4-address-in-java

    private final Pattern pattern = Pattern.compile(AppParameters.validIP);
    private final Pattern pattern2 = Pattern.compile(AppParameters.validFREQUENCY);

    public boolean isvalidIP(final String ip) {
        return pattern.matcher(ip).matches();
    }

    public boolean isvalidFrequency(final String frequency) {
        return pattern2.matcher(frequency).matches();
    }

    private SettingsActivity settingsActivity;

    public SaveListener(SettingsActivity settingsActivity) {

        this.settingsActivity = settingsActivity;
    }

    @Override
    public void onClick(View view) {
        EditText editIP = settingsActivity.findViewById(R.id.editIP);
        EditText editPORT = settingsActivity.findViewById(R.id.editPORT);

        CheckBox saveIP = settingsActivity.findViewById(R.id.checkBoxSaveIP);
        CheckBox savePORT = settingsActivity.findViewById(R.id.checkBoxSavePORT);

        CheckBox saveFrequency = settingsActivity.findViewById(R.id.checkBoxSaveFrequency);
        EditText editFREQUENCY = settingsActivity.findViewById(R.id.editFREQUENCY);

        final String PREF_FILE_NAME = "PrefFile";
        SharedPreferences preferences = settingsActivity.getSharedPreferences(PREF_FILE_NAME, settingsActivity.MODE_PRIVATE);

        boolean saved = false;
        if (saveIP.isChecked()) {
            try {
                String newvalue = editIP.getText().toString();
                if (isvalidIP(newvalue)) {
                    //if (AppParameters.ip.equals(newvalue) == false) {
                    //    AppParameters.reconnect = true;
                    //}
                    AppParameters.ip = newvalue;

                    // Write Shared Preferences
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("storedIP", newvalue);
                    editor.commit();
                    saved = true;
                } else {
                    Toast.makeText(settingsActivity, "IP andress is invalid !!", Toast.LENGTH_LONG).show();
                }
            } catch (Exception ex) {
                Toast.makeText(settingsActivity, "Something went wrong, new IP andress was not stored !!", Toast.LENGTH_LONG).show();
            }
        }

        if (savePORT.isChecked()) {
            try {
                String newvalue = editPORT.getText().toString();
                int v = Integer.parseInt(newvalue);

                //if (AppParameters.port != v) {
                //    AppParameters.reconnect = true;
                //}

                // Write Shared Preferences
                AppParameters.port = v;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("storedPort", v);
                editor.commit();
                saved = true;
            } catch (Exception ex) {
                Toast.makeText(settingsActivity, "Something went wrong, new port was not stored !!", Toast.LENGTH_LONG).show();
            }
        }

        if (saveFrequency.isChecked()) {
            try {
                String newvalue = editFREQUENCY.getText().toString();
                if (isvalidFrequency(newvalue)) {
                    int v = Integer.parseInt(newvalue);

                    if (AppParameters.frequency != v) {
                        AppParameters.frequency = v;
                    }
                        // Write Shared Preferences
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("storedFrequency", v);
                        editor.commit();
                        saved = true;

                        // publish MQTT TOPIC
                        Command cmd = new Command("frequency", v);
                        Thread t = new Thread(new ControllerRunnable(AppParameters.ip, "" + AppParameters.port, cmd));
                        t.start();

                    } else {
                        Toast.makeText(settingsActivity, "Frequency is invalid !! Valid frequencies are [1-9] sec", Toast.LENGTH_LONG).show();
                    }


            } catch (Exception ex) {
                Toast.makeText(settingsActivity, "Something went wrong, new frequency was not stored !!", Toast.LENGTH_LONG).show();
            }
        }

        if (saved) {
            Toast.makeText(settingsActivity, "Settings saved", Toast.LENGTH_LONG).show();
        }
    }
}
