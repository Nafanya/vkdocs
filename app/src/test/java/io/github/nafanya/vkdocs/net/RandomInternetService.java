package io.github.nafanya.vkdocs.net;

import io.github.nafanya.vkdocs.Utils;
import io.github.nafanya.vkdocs.net.base.InternetService;

/**
 * Created by pva701 on 24.02.16.
 */

public class RandomInternetService implements InternetService {
    @Override
    public boolean hasInternetConnection() {
        return Utils.random.nextBoolean();
    }

    @Override
    public void addListener(InternetStateListener listener) {

    }

    @Override
    public void removeListener(InternetStateListener listener) {

    }
}
