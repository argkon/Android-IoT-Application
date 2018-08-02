package com.example.panos.sub;

import com.example.panos.controller.Controller;

public class InterruptorForFlash implements Runnable {
    private int duration;
    private Controller controller;

    public InterruptorForFlash(Controller controller, int duration) {
        this.controller = controller;
        this.duration = duration;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(duration);
            controller.disableFlash();
            controller.clearFlashInterruptors();
        } catch (InterruptedException ex) {
            return;
        }
    }
}
