package com.example.panos.controller;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.panos.Connectivity.ConnectionMode;
import com.example.panos.client.R;
import com.example.panos.parameters.AppParameters;
import com.example.panos.sub.InterruptorForFlash;
import com.example.panos.sub.InterruptorForMusic;
import com.example.panos.sub.MqttSubscriber;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.List;

public class Controller {
    private MqttClient sampleClient = null;
    private MqttConnectOptions connOpts = new MqttConnectOptions();
    private MqttSubscriber sub = new MqttSubscriber(this);
    private MemoryPersistence persistence = new MemoryPersistence();
    private String[] topics;
    private String broker;
    private String clientId;
    private Camera camera;
    private Camera.Parameters parameters;
    private ConnectionMode connectionMode = new ConnectionMode();


    private boolean flash_enabled = false;

    private MediaPlayer musicMP = null;

    private List<Thread> flashInterruptors = new ArrayList<>();
    private List<Thread> musicInterruptors = new ArrayList<>();

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private Context context;

    public void init() {
        topics = new String[]{AppParameters.musicTopic, AppParameters.flashTopic, AppParameters.filesTopic};

        broker = String.format("tcp://%s:%d", AppParameters.ip, AppParameters.port);
        clientId = AppParameters.clientId;
    }

    public void initPlayer(Context context) {
        musicMP = MediaPlayer.create(context, R.raw.song);
    }

    public synchronized String subscribeToAll(Context context) {
        try {
            this.context = context;

            broker = String.format("tcp://%s:%d", AppParameters.ip, AppParameters.port);
            sampleClient = new MqttClient(broker, clientId, persistence);

            connOpts.setCleanSession(true);
            sampleClient.setCallback(sub);
            sampleClient.connect(connOpts);
            sampleClient.subscribe(topics);
            return "OK";
        } catch (MqttException me) {
            sampleClient = null;
            return me.getMessage();
        }
    }

    public synchronized String unsubscribeFromAll() {
        if (sampleClient != null) {
            try {
                sampleClient.unsubscribe(topics);
                sampleClient.disconnect();
                sampleClient.close();
                sampleClient = null;
                return "OK";
            } catch (MqttException me) {
                return me.getMessage();
            }
        } else {
            return "OK";
        }
    }


    public boolean isMusic_playing() {
        return musicMP.isPlaying();
    }

    public boolean isFlash_enabled() {
        return flash_enabled;
    }

    public synchronized void startMusic(int duration) {
        if (duration == 0) {
            if (musicMP.isPlaying()) {
                Log.i("INFO", "Music is already playing");
            } else {
                musicMP.setLooping(true);
                musicMP.start();
            }
        } else {
            if (!musicMP.isPlaying()) {
                musicMP.setLooping(true);
                musicMP.start();

                // Add new thread in List
                InterruptorForMusic in = new InterruptorForMusic(this, duration);
                Thread t = new Thread(in);
                synchronized (musicInterruptors) {
                    musicInterruptors.add(t);
                }
                t.start();

            } else {

                // Clear List
                if (!musicInterruptors.isEmpty()) {
                    for (Thread in : musicInterruptors) {
                        in.interrupt();
                    }
                    musicInterruptors.clear();
                }

                // Add new thread in List
                InterruptorForMusic in = new InterruptorForMusic(this, duration);
                Thread t = new Thread(in);
                synchronized (musicInterruptors) {
                    musicInterruptors.add(t);
                }
                t.start();
            }
        }
    }

    public synchronized void stopMusic() {
        if (musicMP.isPlaying()) {
            musicMP.pause();
        }
        if (!musicInterruptors.isEmpty()) {
            for (Thread in : musicInterruptors) {
                in.interrupt();
            }
            musicInterruptors.clear();
        }
    }

    public synchronized void enableFlash(int duration) { // Similar to the "startMusic" function
        if (duration == 0) {
            if (flash_enabled) {
                Log.i("INFO", "Flash is already enabled");
            } else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
                camera.startPreview();

                flash_enabled = true;
            }
        } else {
            if (!flash_enabled) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
                camera.startPreview();

                flash_enabled = true;


                InterruptorForFlash in = new InterruptorForFlash(this, duration);
                Thread t = new Thread(in);
                synchronized (flashInterruptors) {
                    flashInterruptors.add(t);
                }
                t.start();
            } else {
                if (!flashInterruptors.isEmpty()) {
                    for (Thread in : flashInterruptors) {
                        in.interrupt();
                    }
                    flashInterruptors.clear();
                }

                InterruptorForFlash in = new InterruptorForFlash(this, duration);
                Thread t = new Thread(in);
                synchronized (flashInterruptors) {
                    flashInterruptors.add(t);
                }
                t.start();
            }
        }
    }

    public synchronized void disableFlash() {
        if (camera != null && parameters != null) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameters);
            camera.stopPreview();

            flash_enabled = false;

            if (!flashInterruptors.isEmpty()) {
                for (Thread in : flashInterruptors) {
                    in.interrupt();
                }
                flashInterruptors.clear();
            }
        }
    }

    public void initCamera(Camera camera, Camera.Parameters parameters) {
        this.camera = camera;
        this.parameters = parameters;
    }

    public void destroy() {
        if (camera != null) {
            camera.release();
            camera = null;
        }

        if (musicMP != null && musicMP.isPlaying()) {
            musicMP.stop();
        }
    }

    public boolean verify() {
        if (musicMP == null || camera == null) {
            return false;
        } else {
            return true;
        }
    }

    public void clearFlashInterruptors() {
        synchronized (flashInterruptors) {
            flashInterruptors.clear();
        }
    }

    public void clearMusicInterruptors() {
        synchronized (musicInterruptors) {
            musicInterruptors.clear();
        }
    }

    public ConnectionMode getConnectionMode() {
        return connectionMode;
    }

    public void setConnectionMode(ConnectionMode connectionMode) {
        this.connectionMode = connectionMode;
    }

    public boolean subscribed() {
        return sampleClient != null && sampleClient.isConnected();
    }
}

