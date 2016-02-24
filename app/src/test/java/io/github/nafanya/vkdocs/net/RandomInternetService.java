package io.github.nafanya.vkdocs.net;

import io.github.nafanya.vkdocs.Utils;

/**
 * Created by pva701 on 24.02.16.
 */

public class RandomInternetService implements InternetService {
    @Override
    public boolean hasInternetConnection() {
        return Utils.random.nextBoolean();
    }
}
