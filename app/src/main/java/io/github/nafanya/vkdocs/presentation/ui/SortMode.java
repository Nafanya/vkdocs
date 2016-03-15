package io.github.nafanya.vkdocs.presentation.ui;

import java.util.HashMap;

/**
 * Created by nafanya on 3/12/16.
 */
public enum SortMode {

    DATE, SIZE, NAME;

    private static HashMap<Integer, SortMode> to;
    private static HashMap<SortMode, Integer> from;
    static {
        to = new HashMap<>();
        from = new HashMap<>();
        final SortMode[] v = SortMode.values();
        for (int i = 0; i < v.length; i++) {
            to.put(i, v[i]);
            from.put(v[i], i);
        }
    }

    public static int toInt(SortMode mode) {
        return from.get(mode);
    }

    public static SortMode toMode(int code) {
        return to.get(code);
    }

}
