package com.labs.lab3.field.checker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.labs.lab3.field.Coords;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handling visualization of player moves
 */
public class Checkers {
    public static final double CHECKER_BORDER_MULTIPLIER = 1.15;
    public static final double QUEEN_INNER_CIRCLE_MULTIPLIER = 0.7;
    public static final double CHECKER_RADIUS_MULTIPLIER = 0.35;
    private int cellSize;
    private int checkerSize;
    private int fieldSize;
    private Piece[][] checkersTable;
    private final Paint queenPaint;

    public Checkers(int fieldSize) {
        this.fieldSize = fieldSize;
        initCheckers();
        queenPaint = new Paint();
        queenPaint.setColor(Color.rgb(50, 50, 50));
    }

    private void initCheckers() {
        checkersTable = new Piece[fieldSize][fieldSize];

        initWhites();
        initBlacks();
    }

    private void initWhites() {
        for (int row = 0; row < 3; row++) {
            initRow(row, PieceColor.WHITE);
        }
    }

    private void initBlacks() {
        for (int row = fieldSize - 1; row > fieldSize - 3 - 1; row--) {
            initRow(row, PieceColor.BLACK);
        }
    }

    private void initRow(int row, PieceColor color) {
        for (int i = 0; i < fieldSize; i++) {
            if ((row + i) % 2 == 1)
                checkersTable[row][i] = new Piece(color);
        }
    }

    /**
     * Draw pieces on the table
     */
    public void draw(Canvas canvas) {
        cellSize = canvas.getWidth() / fieldSize;
        checkerSize = (int) (cellSize * CHECKER_RADIUS_MULTIPLIER);
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                if ((j + i) % 2 == 1 &&
                        checkersTable[i][j] != null) {
                    drawPiece(i, j, checkersTable[i][j], canvas);
                }
            }
        }
    }

    private void drawPiece(int row, int column, Piece piece, Canvas canvas) {
        int cx = column * cellSize + cellSize / 2;
        int cy = row * cellSize + cellSize / 2;
        canvas.drawCircle(cx, cy, (float) (checkerSize * CHECKER_BORDER_MULTIPLIER), queenPaint);
        canvas.drawCircle(cx, cy, checkerSize, piece.updatePaint());
        if (piece.getState() == CheckerPieceType.QUEEN) {
            canvas.drawCircle(cx, cy, (int) (checkerSize * QUEEN_INNER_CIRCLE_MULTIPLIER), queenPaint);
        }
    }

    /**
     * Get piece by selected coords
     * @param x x coord of piece
     * @param y y coord of piece
     * @return piece which located by selected coords
     */
    public Piece getPiece(int x, int y) {
        if (border(x) && border(y))
            return checkersTable[y][x];
        return null;
    }

    public Piece getPiece(Coords coords) {
        return checkersTable[coords.y][coords.x];
    }

    public Coords find(Piece piece) {
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                if (checkersTable[i][j] == null)
                    continue;
                if (checkersTable[i][j].equals(piece))
                    return new Coords(j, i);
            }
        }
        return null;
    }

    /**
     * Remove piece from table
     * @param x x coord of piece
     * @param y y coord of piece
     */
    public void remove(int x, int y) {
        checkersTable[y][x] = null;
    }

    /**
     * Move piece to selected coord
     * @param piece A piece that will move
     * @param x x coord of move
     * @param y y coord of move
     * @return
     */
    public boolean move(Piece piece, int x, int y) {
        boolean haveBeaten = false;
        Coords found = find(piece);
        if (found != null && !found.equals(new Coords(x, y)))
            haveBeaten = beat(found.x, found.y, x, y);
        checkersTable[y][x] = piece;
        if (found != null) {
            remove(found.x, found.y);
        }
        return haveBeaten;
    }

    private boolean beat(int xStart, int yStart,
                        int xEnd, int yEnd) {
        boolean haveBeaten = false;
        int xDir = xEnd - xStart;
        int yDir = yEnd - yStart;
        if (xDir == 0 || yDir == 0)
            return false;
        xDir = xDir / Math.abs(xDir);
        yDir = yDir / Math.abs(yDir);
        int xCurr = xStart;
        int yCurr = yStart;
        while (xEnd - xCurr != 0) {
            xCurr += xDir;
            yCurr += yDir;
            if (checkersTable[yCurr][xCurr] != null)
                haveBeaten = true;
            checkersTable[yCurr][xCurr] = null;
        }
        return haveBeaten;
    }

    /**
     * Transforms pawns in the last row of the opponent into a queen
     */
    public void updateQueens() {
        queensRow(0, PieceColor.BLACK);
        queensRow(fieldSize - 1, PieceColor.WHITE);
    }

    private void queensRow(int i, PieceColor color) {
        for (Piece piece : checkersTable[i]) {
            if (piece == null)
                continue;
            if (piece.getColor() == color) {
                piece.setState(CheckerPieceType.QUEEN);
            }
        }
    }

    public int count(PieceColor color) {
        int cnt = 0;
        for (Piece[] row : checkersTable) {
            for (Piece piece : row) {
                if (piece != null && piece.getColor() == color)
                    cnt++;
            }
        }
        return cnt;
    }

    public boolean isDraw(PieceColor color) {
        Map<Piece, List<Coords>> available = getAvailableListByColor(color);
        for (Map.Entry<Piece, List<Coords>> entry : available.entrySet()) {
            if (!entry.getValue().isEmpty())
                return false;
        }
        return true;
    }

    public List<Coords> buildAvailable(Piece piece) {
        Coords coords = find(piece);
        int x = coords.x;
        int y = coords.y;
        List<Coords> res = new ArrayList<>();
        if (piece.getState() == CheckerPieceType.PAWN) {
            if (piece.getColor() == PieceColor.BLACK) {
                tryMove(x, y, -1, -1, piece, res);
                tryMove(x, y, 1, -1, piece, res);

                tryBeat(x, y, -1, 1, piece, res);
                tryBeat(x, y, 1, 1, piece, res);
            } else {
                tryBeat(x, y, -1, -1, piece, res);
                tryBeat(x, y, 1, -1, piece, res);

                tryMove(x, y, -1, 1, piece, res);
                tryMove(x, y, 1, 1, piece, res);
            }

        } else if (piece.getState() == CheckerPieceType.QUEEN) {
            moveInVector(x, y, 1, 1, piece, res);
            moveInVector(x, y, 1, -1, piece, res);
            moveInVector(x, y, -1, 1, piece, res);
            moveInVector(x, y, -1, -1, piece, res);
        }

        return res;
    }

    private void moveInVector(int x, int y, int vx, int vy, Piece current, List<Coords> res) {
        while (border(x + vx) && border(y + vy)) {
            x = x + vx;
            y = y + vy;
            Piece other = getPiece(x, y);
            if (other != null) {
                if (other.getColor() == current.getColor())
                    return;
                if (border(x + vx) && border(y + vy)) {
                    other = getPiece(x + vx, y + vy);
                    if (other == null) {
                        res.add(new Coords(x + vx, y + vy));
                    }
                    return;
                }
            } else {
                res.add(new Coords(x, y));
            }
        }
    }

    private void beatInVector(int x, int y, int vx, int vy, Piece current, List<Coords> res) {
        while (border(x + vx) && border(y + vy)) {
            x = x + vx;
            y = y + vy;
            Piece other = getPiece(x, y);
            if (other != null) {
                if (other.getColor() == current.getColor())
                    return;
                if (border(x + vx) && border(y + vy)) {
                    other = getPiece(x + vx, y + vy);
                    if (other == null) {
                        res.add(new Coords(x + vx, y + vy));
                    }
                    return;
                }
            }
        }
    }

    private void tryMove(int x, int y, int dx, int dy, Piece current, List<Coords> res) {
        if (border(x, dx) && border(y, dy)) {
            Piece pieceOther = getPiece(x + dx, y + dy);
            if (pieceOther == null) {
                res.add(new Coords(x + dx, y + dy));
            } else if (pieceOther.getColor() != current.getColor()
                    && border(x, 2 * dx)
                    && border(y, 2 * dy)) {
                pieceOther = getPiece(x + 2 * dx, y + 2 * dy);
                if (pieceOther == null) {
                    res.add(new Coords(x + 2 * dx, y + 2 * dy));
                }
            }
        }
    }

    public Map<Piece, List<Coords>> getAvailableListByColor(PieceColor color) {
        Map<Piece, List<Coords>> res = new HashMap<>();
        for (Piece[] row : checkersTable) {
            for (Piece piece : row) {
                if (piece == null)
                    continue;
                if (piece.getColor() == color) {
                    List<Coords> availableForThisChecker = buildAvailable(piece);
                    res.put(piece, availableForThisChecker);
                }
            }
        }
        return res;
    }

    private void tryBeat(int x, int y, int dx, int dy, Piece current, List<Coords> res) {
        if (border(x, dx) && border(y, dy)) {
            Piece pieceOther = getPiece(x + dx, y + dy);
            if (pieceOther != null && pieceOther.getColor() != current.getColor()
                    && border(x, 2 * dx)
                    && border(y, 2 * dy)) {
                pieceOther = getPiece(x + 2 * dx, y + 2 * dy);
                if (pieceOther == null) {
                    res.add(new Coords(x + 2 * dx, y + 2 * dy));
                }
            }
        }
    }

    private boolean border(int a, int da) {
        return border(a + da);
    }

    private boolean border(int a) {
        return (a >= 0) && a < fieldSize;
    }

    public List<Coords> canBeat(Piece piece) {
        List<Coords> res = new ArrayList<>();
        Coords coords = find(piece);
        int x = coords.x;
        int y = coords.y;
        if (piece.getState() == CheckerPieceType.PAWN) {
            tryBeat(x, y, -1, 1, piece, res);
            tryBeat(x, y, 1, 1, piece, res);
            tryBeat(x, y, -1, -1, piece, res);
            tryBeat(x, y, 1, -1, piece, res);
        } else {
            beatInVector(x, y, 1, 1, piece, res);
            beatInVector(x, y, 1, -1, piece, res);
            beatInVector(x, y, -1, 1, piece, res);
            beatInVector(x, y, -1, -1, piece, res);
        }
        return res;
    }
}
