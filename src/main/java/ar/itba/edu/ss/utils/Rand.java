package ar.itba.edu.ss.utils;

import java.util.Random;

public class Rand {
    private static final Random INSTANCE = new Random();

    public static Random getInstance() {
        return INSTANCE;
    }
}
