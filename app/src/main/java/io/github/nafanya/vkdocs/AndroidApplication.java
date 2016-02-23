package io.github.nafanya.vkdocs;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;

import timber.log.Timber;

/**
 * Created by Nikita Yaschenko on 2/24/16.
 */
public class AndroidApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // init database
        FlowManager.init(this);

        // enable logging
        Timber.plant(new Timber.DebugTree());
    }
}
