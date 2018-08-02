package com.example.panos.sub;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.panos.controller.Controller;
import com.example.panos.parameters.AppParameters;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttSubscriber implements MqttCallback {

    private final Controller controller;

    public MqttSubscriber(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.i("INFO", "connectionLost");
    }

    private void myLogAndToast(String title, final String content, boolean showtoast) {
        Log.i(title, content);

        if (showtoast) { // Send to main thread a message to show a toast
            if (controller.getContext() != null) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Context con = controller.getContext();
                        Toast.makeText(con, content, Toast.LENGTH_LONG).show();
                    }
                }, 0);
            }
        }
    }


    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String m = new String(message.getPayload());

        myLogAndToast("INFO", "messageArrived >>>>>>>>>>> " + m, false);

        if (m.startsWith("MUSICON")) {
            int x = m.indexOf(" ") + 1;
            String param = m.substring(x);
            int duration;
            try {
                duration = Integer.parseInt(param.trim());
            } catch (Exception ex) {
                duration = -1;
            }

            if (duration > 0) {
                if (controller.isMusic_playing()) {
                    myLogAndToast("INFO", "Music is already playing. It will be still playing for " + duration + " ms", true);
                    controller.startMusic(duration);
                } else {
                    myLogAndToast("INFO", "Music is not playing. It will start playing and will be completed in " + duration + " ms", false);
                    controller.startMusic(duration);
                }
            } else {
                controller.startMusic(duration);
            }
        } else if (m.equals("MUSICOFF")) {
            if (!controller.isMusic_playing()) {
                myLogAndToast("INFO", "Music is already off", true);
            } else {
                myLogAndToast("INFO", "Music is playing. It is going to be stopped", false);
                controller.stopMusic();
            }

        } else if (m.startsWith("FLASHON")) {
            int x = m.indexOf(" ") + 1;
            String param = m.substring(x);
            int duration;

            try {
                duration = Integer.parseInt(param.trim());
            } catch (Exception ex) {
                duration = -1;
            }

            if (controller.isFlash_enabled()) {
                myLogAndToast("INFO", "Flash is already enabled. It will be enabled for " + duration + " ms", true);
                controller.enableFlash(duration);
            } else {
                myLogAndToast("INFO", "Flash is not enabled. It will be enabled for " + duration + " ms", false);
                controller.enableFlash(duration);
            }

        } else if (m.equals("FLASHOFF")) {
            if (!controller.isFlash_enabled()) {
                myLogAndToast("INFO", "Flash is already not enabled", true);
            } else {
                myLogAndToast("INFO", "Flash is enabled. It is going to be disabled", false);
                controller.disableFlash();
            }
        }
        else if (m.equals(("ON"))){
            controller.startMusic(AppParameters.durationOn); // start music
            controller.enableFlash(AppParameters.durationOn); // enable flash
        }
        else if (m.equals("OFF")){
            controller.stopMusic(); //stop music
            controller.disableFlash(); // disable flash
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.i("INFO", "deliveryComplete");
    }
}