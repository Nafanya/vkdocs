package io.github.nafanya.vkdocs.net.base;

/**
 * Created by pva701 on 24.02.16.
 */
public interface InternetService {
    interface InternetStateListener {
        void onEnableWiFi();
        void onEnableMobile();
        void onEnableNetwork();
    }

    boolean hasInternetConnection();
    void addListener(InternetStateListener listener);
    void removeListener(InternetStateListener listener);
}
