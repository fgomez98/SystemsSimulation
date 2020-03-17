package ar.itba.edu.ss.systems;

import java.util.Arrays;
import java.util.List;

public class Direcctions {

    private int x;
    private int y;

    private Direcctions(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    static Direcctions NEUTRAL = new Direcctions(0, 0);

    static Direcctions UP = new Direcctions(-1, 0);
    static Direcctions DOWN = new Direcctions(1, 0);
    static Direcctions RIGHT = new Direcctions(0, 1);
    static Direcctions LEFT = new Direcctions(0, -1);

    static Direcctions UP_RIGHT = new Direcctions(-1, 1);
    static Direcctions DOWN_RIGHT = new Direcctions(1, 1);
    static Direcctions UP_LEFT = new Direcctions(-1, -1);
    static Direcctions DOWN_LEFT = new Direcctions(1, -1);

    static List<Direcctions> L_DIRECTIONS = Arrays.asList(UP, UP_RIGHT, RIGHT, DOWN_RIGHT, NEUTRAL);
}
