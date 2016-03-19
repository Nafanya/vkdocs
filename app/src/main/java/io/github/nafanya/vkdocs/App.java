package io.github.nafanya.vkdocs;

import android.app.Application;
import android.content.Intent;
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
import io.github.nafanya.vkdocs.domain.download.InterruptableDownloadManager;
import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.events.LruEventBus;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import io.github.nafanya.vkdocs.net.InternetService;
import io.github.nafanya.vkdocs.net.InternetServiceImpl;
import io.github.nafanya.vkdocs.presentation.ui.views.LoginActivity;
import io.github.nafanya.vkdocs.utils.FileFormatter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subjects.ReplaySubject;
import timber.log.Timber;

public class App extends Application {

    private EventBus eventBus;
    private InterruptableDownloadManager downloadManager;
    private DocumentRepository repository;
    private FileFormatter fileFormatter;
    private InternetService internetService;

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

        fileFormatter = new FileFormatter(this);

        DatabaseRepository databaseRepository = new DatabaseRepositoryImpl(new DbMapper(new DownloadRequestMapper()));
        NetworkRepository networkRepository = new NetworkRepositoryImpl(new InternetServiceImpl(), new NetMapper());
        repository = new DocumentRepositoryImpl(databaseRepository, networkRepository);
        internetService = new InternetServiceImpl();

        createIfNotExist(getAppCacheRoot());
        createIfNotExist(getAppOfflineRoot());

        /*ConnectableObservable<Integer> ob = Observable.create(
                new Observable.OnSubscribe<Integer>() {
           @Override
           public void call(Subscriber<? super Integer> subscriber) {
               Timber.d("here nahuj");
               for (int i = 1; i <= 5; ++i) {
                   try {
                       Thread.sleep(1000);
                       subscriber.onNext(i);
                   } catch (Exception ignore) {
                   }
                   //subscriber.onError(new RuntimeException("lil"));
               }
           }
       }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).replay(1);
        ob.connect();*/


        /*ReplaySubject<Integer> ob = ReplaySubject.createWithSize(1);
        ob.replay(1);
        ob.onNext(1);
        ob.onNext(2);
        ob.onNext(3);
        ob.onError(new RuntimeException("LIL"));

        for (int i = 1; i <= 3; ++i) {
            Timber.d("sub %d", i);
            final int number = i;
            ob.subscribe(new Subscriber<Integer>() {
                @Override
                public void onCompleted() {
                    Timber.d("my on complete: %d", number);
                }

                @Override
                public void onError(Throwable e) {
                    Timber.d("my exception: %d on error = %s", number, e);
                }

                @Override
                public void onNext(Integer integer) {
                    Timber.d("my number: %d on next = %d", number, integer);
                }
            });
        }*/
    }

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
}
