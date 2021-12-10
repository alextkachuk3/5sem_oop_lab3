package com.labs.lab3.field;

/**
 * Coords class. Using for working with cells on board.
 */
public class Coords {
    public final int x;
    public final int y;

    public Coords(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Coords toCoords(float x, float y, int fieldSize, int cellSize) {
        Coords res = new Coords((int) (x / cellSize), (int) (y / cellSize));
        if (res.x >= fieldSize || res.y >= fieldSize)
            return null;
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Coords))
            return false;

        Coords coords = (Coords) (o);
        return coords.x == x && coords.y == y;
    }
}
