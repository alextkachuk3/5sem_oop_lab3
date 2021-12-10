package com.labs.lab3.field.checker;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * The class of player and AI pieces.
 * Paint pieces and check type of piece
 */
public class Piece {
    private CheckerPieceType state = CheckerPieceType.PAWN;
    private final PieceColor color;
    private final Paint paint;

    public static final int whitePawn = Color.rgb(200, 200, 200);
    public static final int blackPawn = Color.rgb(0, 0, 0);

    public Piece(PieceColor pieceColor) {
        color = pieceColor;
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
    }

    public PieceColor getColor() {
        return color;
    }

    public CheckerPieceType getState() {
        return state;
    }

    public void setState(CheckerPieceType state) {
        this.state = state;
        updatePaint();
    }

    public Paint updatePaint() {
        switch (color) {
            case BLACK:
                paint.setColor(blackPawn);
                break;
            case WHITE:
                paint.setColor(whitePawn);
                break;
        }
        return paint;
    }
}
