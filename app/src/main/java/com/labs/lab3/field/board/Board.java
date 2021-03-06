package com.labs.lab3.field.board;

import android.graphics.Canvas;

import com.labs.lab3.field.Coords;

/**
 * Chess board class
 * Init board cells and theirs color. Also setting cells status
 */
public class Board {
    private final int fieldSize;
    private int cellSize;
    private Cell[][] cells;


    public Board(int fieldSize) {
        this.fieldSize = fieldSize;
        initCells(fieldSize, fieldSize);
    }

    private void initCells(int columns, int rows) {
        cells = new Cell[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if ((i + j) % 2 == 0) {
                    cells[j][i] = new Cell(CellColor.WHITE);
                } else
                    cells[j][i] = new Cell(CellColor.BLACK);
            }
        }
    }

    public void draw(Canvas canvas) {
        cellSize = canvas.getWidth() / fieldSize;
        drawGrid(fieldSize, fieldSize, canvas);
    }


    private void drawGrid(int columns, int rows, Canvas canvas) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                drawCell(cells[j][i], j, i, canvas);
            }
        }
    }

    private void drawCell(Cell cell, int x, int y, Canvas canvas) {

        canvas.drawRect(x * cellSize, y * cellSize,
                x * cellSize + cellSize, y * cellSize + cellSize,
                cell.getPaint());
    }

    public Cell getCell(Coords coords) {
        if (!border(coords))
            return null;
        return cells[coords.x][coords.y];
    }

    public int getCellSize() {
        return cellSize;
    }

    public void unselectAll() {
        for (Cell[] cellRow : cells) {
            for (Cell cell : cellRow) {
                cell.setCurrentState(CellStatus.IDLE);
            }
        }
    }

    private boolean border(Coords coords) {
        return border(coords.x) && border(coords.y);
    }

    private boolean border(int a) {
        return (a >= 0) && a < fieldSize;
    }

}
