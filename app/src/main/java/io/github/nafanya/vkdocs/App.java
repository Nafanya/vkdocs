package io.github.nafanya.vkdocs;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

import java.io.File;
import java.util.concurrent.Executors;

import io.github.nafanya.vkdocs.data.DocumentRepositoryImpl;
import io.github.nafanya.vkdocs.data.database.DbRequestStorage;
import io.github.nafanya.vkdocs.data.database.mapper.DbMapper;
import io.github.nafanya.vkdocs.data.database.mapper.DownloadRequestMapper;
import io.github.nafanya.vkdocs.data.database.repository.DatabaseRepository;
import io.github.nafanya.vkdocs.data.database.repository.DatabaseRepositoryImpl;
import io.github.nafanya.vkdocs.data.net.NetworkRepository;
import io.github.nafanya.vkdocs.data.net.NetworkRepositoryImpl;
import io.github.nafanya.vkdocs.data.net.mapper.NetMapper;
import io.github.nafanya.vkdocs.net.base.OfflineManager;
import io.github.nafanya.vkdocs.net.impl.InterruptableOfflineManager;
import io.github.nafanya.vkdocs.net.impl.download.InterruptableDownloadManager;
import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.events.LruEventBus;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import io.github.nafanya.vkdocs.net.base.InternetService;
import io.github.nafanya.vkdocs.net.impl.InternetServiceImpl;
import io.github.nafanya.vkdocs.presentation.ui.views.LoginActivity;
import io.github.nafanya.vkdocs.utils.FileFormatter;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class App extends Application {

    private EventBus eventBus;
    private InterruptableDownloadManager downloadManager;
    private DocumentRepository repository;
    private FileFormatter fileFormatter;
    private InternetServiceImpl internetService;
    private OfflineManager offlineManager;

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
        //VKSdk.initialize(this).withPayments();
        VKSdk.initialize(this);

        // init database
        FlowManager.init(this);

        // enable logging
        Timber.plant(new Timber.DebugTree());

        eventBus = new LruEventBus(10);
        downloadManager = new InterruptableDownloadManager(
                Schedulers.from(Executors.newFixedThreadPool(5)),
                new DbRequestStorage(new DownloadRequestMapper()));

        fileFormatter = new FileFormatter(this);

        DatabaseRepository databaseRepository = new DatabaseRepositoryImpl(new DbMapper(new DownloadRequestMapper()));
        NetworkRepository networkRepository = new NetworkRepositoryImpl(new NetMapper());
        repository = new DocumentRepositoryImpl(databaseRepository, networkRepository);
        internetService = new InternetServiceImpl(this);
        registerReceiver(internetService, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        offlineManager = new InterruptableOfflineManager(internetService, downloadManager, repository, eventBus);

        createIfNotExist("/VKDocs/offline");
        createIfNotExist("/VKDocs/cache");
    }

    private void createIfNotExist(String path) {
        File dir = new File(Environment.getExternalStorageDirectory().getPath() + path);
        if (!dir.exists()) {
            if (!dir.mkdirs())
                throw new RuntimeException("Can't create folder " + path);
        }
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

    public InternetService getInternetService() {
        return internetService;
    }

    public FileFormatter getFileFormatter() {
        return fileFormatter;
    }

    public OfflineManager getOfflineManager() {
        return offlineManager;
    }
}
