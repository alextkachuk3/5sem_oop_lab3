package com.labs.lab3.field;

import android.graphics.Canvas;

import com.labs.lab3.AI.AI;
import com.labs.lab3.GameView;
import com.labs.lab3.field.checker.Piece;
import com.labs.lab3.field.checker.PieceColor;
import com.labs.lab3.field.checker.Checkers;
import com.labs.lab3.field.board.Cell;
import com.labs.lab3.field.board.CellColor;
import com.labs.lab3.field.board.CellStatus;
import com.labs.lab3.field.board.Board;

import java.util.ArrayList;
import java.util.List;

/**
 * Class controlling player moves
 */
public class FieldController {
    public static final PieceColor playerSide = PieceColor.BLACK;
    private final GameView caller;
    private final int fieldSize;

    private final Board board;
    private final Checkers checkers;

    private Piece selected;
    private PieceColor gameState = PieceColor.BLACK;
    private volatile boolean playerBeatRow = false;
    private List<Coords> availableCoords = new ArrayList<>();

    public FieldController(int fieldSize, GameView caller) {
        this.fieldSize = fieldSize;
        this.caller = caller;
        board = new Board(fieldSize);
        checkers = new Checkers(fieldSize);
    }

    public void draw(Canvas canvas) {
        board.draw(canvas);
        checkers.draw(canvas);
    }


    public void startBotCycle() {
        while (!Thread.currentThread().isInterrupted()) {
            if (gameState == other(playerSide)) {
                if (!playerBeatRow) {
                    startBotTurn();
                    reverseState();
                    updateQueens();
                }
            }
        }
    }

    /**
     * Function which call after player click
     * If player clicks on his piece, then the cells available for move will be colored green
     * If the player clicks on a green cell, then he moves the selected piece to that cell.
     * @param x x coord
     * @param y y coord
     */
    public void activatePlayer(float x, float y) {
        if (gameState == playerSide) {
            Coords tapCoords = Coords.toCoords(x, y, fieldSize, board.getCellSize());

            if (selected == null) {
                trySelect(tapCoords);
                callUpdate();
            } else {

                if (coordsInAvailableCells(tapCoords)) {
                    playerBeatRow = doPlayerStep(tapCoords);
                    updateQueens();
                    Coords lastPosition = checkers.find(selected);

                    if (playerBeatRow && canBeatMore(tapCoords)) {
                        playerBeatRow = true;
                        trySelectBeatable(lastPosition);
                    } else {
                        playerBeatRow = false;
                        unselectAll();
                    }
                } else {
                    unselectAll();
                    callUpdate();
                    return;
                }
                callUpdate();

                if (!playerBeatRow) {
                    unselectAll();
                    reverseState();
                }
            }
        }
    }

    /**
     * Function for bot turn
     */
    public void startBotTurn() {
        boolean botBeatRow;
        Coords checkerCoords = AI.chooseChecker(checkers, other(playerSide));
        if (checkerCoords == null)
            return;
        Piece pieceBot = checkers.getPiece(checkerCoords);
        trySelect(checkerCoords);
        callUpdate();
        Coords moveCoords = AI.chooseMove(checkers, pieceBot);
        if (moveCoords == null)
            return;
        botBeatRow = checkers.move(pieceBot, moveCoords.x, moveCoords.y);
        unselectAll();
        callUpdate();

        while (botBeatRow && canBeatMore(moveCoords)) {
            unselectAll();
            trySelectBeatable(moveCoords);
            callUpdate();
            moveCoords = AI.chooseMove(checkers, pieceBot);
            if (moveCoords != null)
                botBeatRow = checkers.move(pieceBot, moveCoords.x, moveCoords.y);
            else botBeatRow = false;
            callUpdate();
        }
        unselectAll();
    }

    public void callUpdate() {
        if (caller != null)
            caller.invalidate();
    }

    private boolean doPlayerStep(Coords tapCoords) {
        Coords oldCoords = checkers.find(selected);
        if (oldCoords.equals(tapCoords))
            return false;
        return checkers.move(selected, tapCoords.x, tapCoords.y);
    }

    public boolean canBeatMore(Coords lastPosition) {
        if (lastPosition == null)
            return false;
        Piece piece = checkers.getPiece(lastPosition);
        List<Coords> beatable = checkers.canBeat(piece);
        return !beatable.isEmpty();
    }

    private boolean coordsInAvailableCells(Coords tapCoords) {
        if (tapCoords == null)
            return false;
        for (Coords coords : availableCoords) {
            if (tapCoords.equals(coords))
                return true;
        }
        return false;
    }

    private void trySelect(Coords coords) {
        if (coords == null)
            return;
        unselectAll();
        Cell cell = board.getCell(coords);
        Piece piece = checkers.getPiece(coords);

        if (cell != null && piece != null &&
                cell.getCellColor() == CellColor.BLACK && piece.getColor().equals(gameState)) {
            cell.setCurrentState(CellStatus.ACTIVE);
            selected = piece;

            activateAvailable(piece);
        }
    }


    private void trySelectBeatable(Coords coords) {
        if (coords == null)
            return;
        Cell cell = board.getCell(coords);
        Piece piece = checkers.getPiece(coords);

        if (cell != null && piece != null &&
                cell.getCellColor() == CellColor.BLACK && piece.getColor().equals(gameState)) {
            cell.setCurrentState(CellStatus.ACTIVE);
            selected = piece;

            activateAvailableBeatable(piece);
        }
    }


    public void reverseState() {
        gameState = other(gameState);
    }

    public static PieceColor other(PieceColor color) {
        if (color == PieceColor.BLACK)
            return PieceColor.WHITE;
        else
            return PieceColor.BLACK;
    }

    private void unselectAll() {
        selected = null;
        availableCoords.clear();
        board.unselectAll();
    }

    private void activateAvailable(Piece piece) {
        if (activationPreparation(piece)) return;
        availableCoords = checkers.buildAvailable(piece);
        setActiveToAvailable();
    }

    private void activateAvailableBeatable(Piece piece) {
        if (activationPreparation(piece)) return;
        availableCoords = checkers.canBeat(piece);
        setActiveToAvailable();
    }

    private boolean activationPreparation(Piece piece) {
        if (piece == null)
            return true;
        availableCoords.clear();
        board.unselectAll();
        return false;
    }

    private void setActiveToAvailable() {
        for (Coords coords : availableCoords) {
            board.getCell(coords).setCurrentState(CellStatus.ACTIVE);
        }
    }

    private void updateQueens() {
        checkers.updateQueens();
    }

    public String getStatus() {
        if (checkers.count(PieceColor.BLACK) == 0)
            return "WHITE WON";
        else if (checkers.count(PieceColor.WHITE) == 0)
            return "BLACK WON";
        else if (checkers.isDraw(gameState))
            return "DRAW";
        else return null;
    }
}
