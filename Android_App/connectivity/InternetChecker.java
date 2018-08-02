package com.example.panos.Connectivity;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.panos.client.MainActivity;
import com.example.panos.controller.Controller;
import com.example.panos.parameters.AppParameters;

public class InternetChecker implements Runnable {
    private final Controller controller;
    private int poll;
    private MainActivity mainActivity;
    private volatile boolean isRunning = true;
    private Handler mHandler;
    private boolean flagNetworkAvailable = false;
    private AlertDialog connectionDialog;
    private boolean dialogUpMqtt = false;
    private boolean dialogUpInternet = false;

    public InternetChecker(MainActivity mainActivity, Controller controller, int poll) {
        this.controller = controller;
        this.poll = poll;
        this.mainActivity = mainActivity;
        this.mHandler = mainActivity.mHandler;
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
    public void run() {

        while (isRunning) {
            try {
                Thread.sleep(poll);

                ConnectivityManager cn;
                cn = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo nf = cn.getActiveNetworkInfo();


                WifiManager wifiManager;
                wifiManager = (WifiManager) mainActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                if (controller.getConnectionMode().isAutomatic()) {
                    if (nf != null && nf.isConnected() && wifiManager.isWifiEnabled()) { // internet
                        flagNetworkAvailable = true;

                        if (!controller.subscribed()) {
                            controller.subscribeToAll(mainActivity);
                            if (controller.subscribed()) {
                                myLogAndToast("INFO", "We are ready", true);
                                AppParameters.flagSub = true;
                                AppParameters.toastUpConnection = true;
                            }
                        }
                    } else {
                        if (flagNetworkAvailable && AppParameters.flagSub) { // internet and MQTT Connection was available at previous sample
                            myLogAndToast("INFO", "MQTT Connection Lost", true);
                        } else if (flagNetworkAvailable) {
                            myLogAndToast("INFO", "Internet Connection Lost", true); // only internet was available at previous sample
                        }
                        flagNetworkAvailable = false;
                        AppParameters.flagSub = false;
                    }
                } else {
                    if (nf != null && nf.isConnected() && wifiManager.isWifiEnabled()) {
                        flagNetworkAvailable = true;

                        if(!controller.subscribed() && AppParameters.toastUpConnection){
                            myLogAndToast("INFO", "You are in the Manual Mode, if you want press the CONNECT button", true);
                            AppParameters.toastUpConnection = false;
                        }

                        if(dialogUpMqtt) {
                            connectionDialog.dismiss();
                            dialogUpMqtt=false;
                        }

                        if(dialogUpInternet){
                            connectionDialog.dismiss();
                            dialogUpMqtt=false;
                        }

                        if (controller.subscribed() && !AppParameters.flagSub) {
                            myLogAndToast("INFO", "We are ready", true);
                            AppParameters.flagSub = true;
                            AppParameters.toastUpConnection = true;
                        }
                    } else {
                        if (flagNetworkAvailable == true) {
                            flagNetworkAvailable = false;

                            if(AppParameters.flagSub){
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                                        builder.setTitle("MQTT Connection Failed...");
                                        builder.setMessage("There is no Internet connection");
                                        builder.setPositiveButton("Wireless Settings", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                                                mainActivity.startActivity(intent);
                                            }
                                        });
                                        builder.setNegativeButton("Cancel", null);
                                        connectionDialog = builder.create();
                                        connectionDialog.show();
                                        dialogUpMqtt = true;
                                    }
                                });
                                AppParameters.toastUpConnection = true;
                                AppParameters.flagSub = false;
                            }else {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                                        builder.setTitle("Connection Failed...");
                                        builder.setMessage("There is no Internet connection");
                                        builder.setPositiveButton("Wireless Settings", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                                                mainActivity.startActivity(intent);
                                            }
                                        });
                                        builder.setNegativeButton("Cancel", null);
                                        connectionDialog = builder.create();
                                        connectionDialog.show();
                                        dialogUpInternet = true;
                                    }
                                });
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {

            }
        }
    }

    public void stop() {
        isRunning = false;
    }


}



