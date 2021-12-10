package com.labs.lab3.field.board;

import android.graphics.Color;
import android.graphics.Paint;


public class Cell {
    private final CellColor cellColor;
    private int colorIdle;

    private final Paint paint;

    public static final int brown = Color.rgb(66, 48, 7);
    public static final int yellow = Color.rgb(176, 161, 25);
    public static final int colorActive = Color.rgb(10, 200, 10);

    public Cell(CellColor color) {
        cellColor = color;
        this.paint = new Paint();

        if (color == CellColor.BLACK) {
            setColorIdle(brown);
        } else if (color == CellColor.WHITE) {
            setColorIdle(yellow);
        }
    }

    public void setColorIdle(int colorIdle) {
        this.colorIdle = colorIdle;
        this.paint.setColor(colorIdle);
    }

    public Paint getPaint() {
        return paint;
    }

    public void setCurrentState(CellStatus currentState) {
        if (currentState == CellStatus.IDLE) {
            paint.setColor(colorIdle);
        } else if (currentState == CellStatus.ACTIVE) {
            paint.setColor(colorActive);
        }
    }

    public CellColor getCellColor() {
        return cellColor;
    }

}
