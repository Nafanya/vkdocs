package io.github.nafanya.vkdocs.presentation.presenter.base;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.List;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.LoadMyDocuments;
import io.github.nafanya.vkdocs.domain.interactor.base.DefaultSubscriber;
import io.github.nafanya.vkdocs.domain.interactor.GetMyDocuments;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import rx.Scheduler;
import rx.Subscriber;
import rx.observers.Subscribers;


public class DocumentsPresenter extends AbstractPresenter {

    public interface Callback {
        void onDatabaseDocuments(List<VKApiDocument> documents);
        void onNetworkDocuments(List<VKApiDocument> documents);
        void onNetworkError(Exception ex);
        void onDatabaseError(Exception ex);
    }

    private GetMyDocuments databaseInteractor;
    private LoadMyDocuments networkInteractor;
    private Subscriber<List<VKApiDocument>> databaseSubscriber = Subscribers.empty();
    private Subscriber<List<VKApiDocument>> networkSubscriber = Subscribers.empty();

    private Callback callback;
    private DocumentRepository repository;

    public DocumentsPresenter(Scheduler observerScheduler, Scheduler subscriberScheduler,
                              EventBus eventBus,
                              DocumentRepository repository) {
        super(observerScheduler, subscriberScheduler);

        this.databaseInteractor = new GetMyDocuments(observerScheduler, subscriberScheduler, eventBus, true, repository);
        this.networkInteractor = new LoadMyDocuments(observerScheduler, subscriberScheduler, eventBus, true, repository);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void loadNetworkDocuments() {
        networkSubscriber = new NetworkSubscriber();
        networkInteractor.execute(networkSubscriber);
    }

    public void loadDatabaseDocuments() {
        databaseSubscriber = new DatabaseSubscriber();
        databaseInteractor.execute(databaseSubscriber);
    }

    @Override
    public void onStop() {
        if (!databaseSubscriber.isUnsubscribed())
            databaseSubscriber.unsubscribe();
        if (!networkSubscriber.isUnsubscribed())
            networkSubscriber.unsubscribe();
    }

    public class DatabaseSubscriber extends DefaultSubscriber<List<VKApiDocument>> {
        @Override
        public void onNext(List<VKApiDocument> vkApiDocuments) {
            if (callback != null)
                callback.onDatabaseDocuments(vkApiDocuments);
        }

        @Override
        public void onError(Throwable e) {
            if (callback != null)
                callback.onDatabaseError((Exception)e);
        }
    }

    public class NetworkSubscriber extends DefaultSubscriber<List<VKApiDocument>> {
        @Override
        public void onNext(List<VKApiDocument> vkApiDocuments) {
            if (callback != null)
                callback.onNetworkDocuments(vkApiDocuments);
        }

        @Override
        public void onError(Throwable e) {
            if (callback != null)
                callback.onNetworkError((Exception) e);
        }
    }
}