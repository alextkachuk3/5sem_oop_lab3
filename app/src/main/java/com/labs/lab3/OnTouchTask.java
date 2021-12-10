package com.labs.lab3;

import android.view.MotionEvent;

import com.labs.lab3.field.FieldController;

class OnTouchTask implements Runnable {
    private final MotionEvent event;
    private final FieldController fieldController;

    OnTouchTask(MotionEvent event, FieldController fieldController) {
        this.event = event;
        this.fieldController = fieldController;
    }

    @Override
    public void run() {
        if (event == null)
            return;
        if (event.getAction() == MotionEvent.ACTION_UP) {
            fieldController.activatePlayer(event.getX(), event.getY());
        }
    }
}