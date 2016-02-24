package io.github.nafanya.vkdocs;

import java.util.Random;

/**
 * Created by pva701 on 24.02.16.
 */
public class Utils {

    private static Random random = new Random(0xB00BC);

    public static int randInt() {
        return random.nextInt(2_000_000_000);
    }

    public static int randInt(int l, int r) {
        return random.nextInt(r - l + 1) + l;
    }

    public static int randInt(int r) {
        return randInt(1, r);
    }
}
