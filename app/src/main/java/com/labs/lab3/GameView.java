package com.labs.lab3;

import static java.util.concurrent.Executors.newFixedThreadPool;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.labs.lab3.field.FieldController;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class GameView extends View {
    private final ExecutorService executor = newFixedThreadPool(5);

    private static final int FIELD_SIZE = 8;
    private final FieldController fieldController;
    private Future botFuture;

    public GameView(Context context) {
        super(context);
        fieldController = new FieldController(FIELD_SIZE, this);

        assignTouchListener();
        startBotThread();
    }

    private void startBotThread() {
        botFuture = executor.submit(() -> {
            try {
                fieldController.startBotCycle();
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private void assignTouchListener() {
        setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Future touchFuture = executor.submit(new OnTouchTask(event, fieldController));
                try {
                    touchFuture.get();
                } catch (Exception e) {
                    Log.e("Touch exception", Arrays.toString(e.getStackTrace()));
                    Thread.currentThread().interrupt();
                }
                return true;
            }
        });
    }

    public void updateView() {
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        fieldController.draw(canvas);
    }

    public Future getBotFuture() {
        return botFuture;
    }

}


