package com.labs.lab3.AI;

import com.labs.lab3.field.Coords;
import com.labs.lab3.field.checker.Piece;
import com.labs.lab3.field.checker.PieceColor;
import com.labs.lab3.field.checker.Checkers;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Checkers AI algorithm class
 */
public class AI {
    private static final Random random = new Random();

    /**
     *
     * @param checkers
     * @param botColor color of pieces for move
     * @return
     */
    public static Coords chooseChecker(Checkers checkers, PieceColor botColor) {
        Map<Piece, List<Coords>> available = checkers.getAvailableListByColor(botColor);
        Coords res = null;
        for (Map.Entry<Piece, List<Coords>> entry : available.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                res = checkers.find(entry.getKey());
                break;
            }
        }
        return res;
    }

    /**
     *
     * @param checkers Checkers
     * @param piece piece which will move
     * @return Return selected move coords
     */
    public static Coords chooseMove(Checkers checkers, Piece piece) {
        List<Coords> available = checkers.buildAvailable(piece);
        if (available.isEmpty())
            return null;
        return available.get(random.nextInt(available.size()));
    }
}
