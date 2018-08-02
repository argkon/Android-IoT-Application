package com.example.panos.sub;

import com.example.panos.controller.Controller;

public class InterruptorForMusic implements Runnable {
    private int duration;
    private Controller controller;

    public InterruptorForMusic(Controller controller, int duration) {
        this.controller = controller;
        this.duration = duration;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(duration);
            controller.stopMusic();
            controller.clearMusicInterruptors();
        } catch (InterruptedException ex) {
            return;
        }
    }
}
