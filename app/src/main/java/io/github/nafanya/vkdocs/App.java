package io.github.nafanya.vkdocs;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.crashlytics.android.Crashlytics;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

import io.fabric.sdk.android.Fabric;
import io.github.nafanya.vkdocs.data.DocumentRepositoryImpl;
import io.github.nafanya.vkdocs.data.UserRepositoryImpl;
import io.github.nafanya.vkdocs.data.database.DbRequestStorage;
import io.github.nafanya.vkdocs.data.database.mapper.DbMapper;
import io.github.nafanya.vkdocs.data.database.mapper.DownloadRequestMapper;
import io.github.nafanya.vkdocs.data.database.repository.DatabaseRepository;
import io.github.nafanya.vkdocs.data.database.repository.DatabaseRepositoryImpl;
import io.github.nafanya.vkdocs.data.net.NetworkRepository;
import io.github.nafanya.vkdocs.data.net.NetworkRepositoryImpl;
import io.github.nafanya.vkdocs.data.net.mapper.NetMapper;
import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.events.LruEventBus;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import io.github.nafanya.vkdocs.domain.repository.UserRepository;
import io.github.nafanya.vkdocs.net.base.CacheManager;
import io.github.nafanya.vkdocs.net.base.InternetService;
import io.github.nafanya.vkdocs.net.base.OfflineManager;
import io.github.nafanya.vkdocs.net.impl.InternetServiceImpl;
import io.github.nafanya.vkdocs.net.impl.InterruptableCacheManager;
import io.github.nafanya.vkdocs.net.impl.InterruptableOfflineManager;
import io.github.nafanya.vkdocs.net.impl.download.InterruptableDownloadManager;
import io.github.nafanya.vkdocs.presentation.ui.activities.LoginActivity;
import io.github.nafanya.vkdocs.presentation.ui.media.AudioPlayerService;
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
    private UserRepository userRepository;
    private CacheManager cacheManager;
    private AudioPlayerService playerService;

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


    public static final int DEFAULT_CACHE_SIZE = 100;//mb

    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        vkAccessTokenTracker.startTracking();
        //VKSdk.initialize(this).withPayments();
        VKSdk.initialize(this);

        // init database
        FlowManager.init(this);

        // enable logging
        Timber.plant(new Timber.DebugTree());

        eventBus = new LruEventBus(100);
        downloadManager = new InterruptableDownloadManager(
                Schedulers.from(Executors.newFixedThreadPool(8)),
                new DbRequestStorage(new DownloadRequestMapper()));

        fileFormatter = new FileFormatter(this);

        DatabaseRepository databaseRepository = new DatabaseRepositoryImpl(new DbMapper(new DownloadRequestMapper()));
        NetworkRepository networkRepository = new NetworkRepositoryImpl(new NetMapper());
        repository = new DocumentRepositoryImpl(databaseRepository, networkRepository);
        internetService = new InternetServiceImpl(this);
        registerReceiver(internetService, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        offlineManager = new InterruptableOfflineManager(internetService, downloadManager, repository, eventBus, getAppCacheRoot());
        cacheManager = new InterruptableCacheManager(eventBus, repository, downloadManager, getAppCacheRoot(), this, DEFAULT_CACHE_SIZE);
        startService(new Intent(this, AudioPlayerService.class));

        try {
            userRepository = new UserRepositoryImpl(this, getAppCacheRoot().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        createIfNotExist(getAppCacheRoot());
        createIfNotExist(getAppOfflineRoot());


        Intent intent = new Intent(this, AudioPlayerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playerService = ((AudioPlayerService.AudioPlayerBinder) service).service();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playerService = null;
        }
    };


    public File getAppCacheRoot() {
        return getExternalCacheDir();
    }

    public File getAppOfflineRoot() {
        return getExternalFilesDir(null);
    }

    private void createIfNotExist(File dir) {
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("Can't create " + dir);
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

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public AudioPlayerService getPlayerService() {
        return playerService;
    }
}
