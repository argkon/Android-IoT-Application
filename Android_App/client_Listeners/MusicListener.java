package com.example.panos.clientlisteners;

import android.view.View;

import com.example.panos.controller.Controller;

public class MusicListener implements View.OnClickListener {
    private Controller controller;

    public MusicListener(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void onClick(View view) {
        if (controller.isMusic_playing()) {
            controller.stopMusic();
        } else {
            controller.startMusic(0);
        }
    }
}
