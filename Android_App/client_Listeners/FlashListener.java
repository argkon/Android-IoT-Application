package com.example.panos.clientlisteners;

import android.view.View;

import com.example.panos.controller.Controller;

public class FlashListener implements View.OnClickListener {
    private Controller controller;

    public FlashListener(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void onClick(View view) {
        if (controller.isFlash_enabled()) {
            controller.disableFlash();
        } else {
            controller.enableFlash(0);
        }
    }
}
