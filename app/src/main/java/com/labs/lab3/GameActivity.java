package com.labs.lab3;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 *
 */
public class GameActivity extends Activity {
    private GameView gameView;
    private LinearLayout gameLayout;
    private Handler gameStatusHandler;
    private String prevStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        gameLayout = findViewById(R.id.game_layout);

        gameView = new GameView(this);
        gameLayout.addView(gameView);

        startNewGame();
        Button newGameButton = findViewById(R.id.new_game_button);
        newGameButton.setOnClickListener(newGameListener);
    }

    private void startNewGame() {
        if (gameView != null && gameView.getBotFuture() != null) {
            gameView.cancel();
            gameView.getBotFuture().cancel(true);
        }
        gameView = new GameView(this);
        gameLayout.removeAllViews();
        gameLayout.addView(gameView);
        gameView.updateView();
        prevStatus = gameView.getStatus();
        gameStatusHandler = new Handler();
        gameStatusHandler.post(gameStatus);
    }

    private final View.OnClickListener newGameListener = v -> {
        if (v.getId() == R.id.new_game_button) {
            startNewGame();
        }
    };

    private Runnable gameStatus = new Runnable() {
        @Override
        public void run() {
            if(gameView != null) {
                String status = gameView.getStatus();
                if(status != null && !gameView.isCancelled()) {
                    if(!status.equals(prevStatus)) {
                        Toast toast = Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT);
                        toast.show();
                        prevStatus = status;
                    }
                }
            }
            if(!gameView.isCancelled()) {
                gameStatusHandler.postDelayed(this, 200);
            }
        }
    };

}
