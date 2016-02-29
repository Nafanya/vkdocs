package io.github.nafanya.vkdocs;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

import java.util.concurrent.Executors;

import io.github.nafanya.vkdocs.data.DocumentRepositoryImpl;
import io.github.nafanya.vkdocs.data.database.DbRequestStorage;
import io.github.nafanya.vkdocs.data.database.mapper.DocsMapper;
import io.github.nafanya.vkdocs.data.database.mapper.DownloadRequestMapper;
import io.github.nafanya.vkdocs.data.database.repository.DatabaseRepository;
import io.github.nafanya.vkdocs.data.database.repository.DatabaseRepositoryImpl;
import io.github.nafanya.vkdocs.data.net.NetworkRepository;
import io.github.nafanya.vkdocs.data.net.NetworkRepositoryImpl;
import io.github.nafanya.vkdocs.domain.download.DownloadRequest;
import io.github.nafanya.vkdocs.domain.download.base.DownloadManager;
import io.github.nafanya.vkdocs.domain.download.InterruptableDownloadManager;
import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.events.LruEventBus;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import io.github.nafanya.vkdocs.net.InternetServiceImpl;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by nafanya on 1/31/16.
 */
public class App extends Application {

    private EventBus eventBus;
    private InterruptableDownloadManager downloadManager;
    private DocumentRepository repository;

    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
                //Toast.makeText(App.this, "AccessToken invalidated", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(App.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    };

    public void onCreate() {
        super.onCreate();

        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this).withPayments();

        // init database
        FlowManager.init(this);

        // enable logging
        Timber.plant(new Timber.DebugTree());

        eventBus = new LruEventBus(10);
        downloadManager = new InterruptableDownloadManager(
                Schedulers.from(Executors.newFixedThreadPool(5)),
                new DbRequestStorage(new DownloadRequestMapper()));

        DatabaseRepository databaseRepository = new DatabaseRepositoryImpl(new DocsMapper());
        NetworkRepository networkRepository = new NetworkRepositoryImpl(new InternetServiceImpl());
        repository = new DocumentRepositoryImpl(databaseRepository, networkRepository);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public InterruptableDownloadManager getDownloadManager() {
        return downloadManager;
    }

    public DocumentRepository getRepository() {
        return repository;
    }
}
