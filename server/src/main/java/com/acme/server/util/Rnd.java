package com.acme.server.util;

import java.util.Random;

public final class Rnd {
    private Rnd() {
    }

    public static int between(int min, int max) {
        Random random = new Random();
        return min + random.nextInt((max - min) + 1);
    }
}
