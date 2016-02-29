package io.github.nafanya.vkdocs.presentation.presenter.base;

import com.vk.sdk.api.model.VKApiDocument;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.LoadMyDocuments;
import io.github.nafanya.vkdocs.domain.interactor.base.DefaultSubscriber;
import io.github.nafanya.vkdocs.domain.interactor.GetMyDocuments;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.Subscribers;
import rx.schedulers.Schedulers;


public class CommonDocumentsPresenter extends BasePresenter implements Serializable {

    public interface Callback {
        void onGetDocuments(List<VKApiDocument> documents);
        void onNetworkError(Exception ex);
        void onDatabaseError(Exception ex);
        void onMakeOffline(Exception ex);
        void onRename(Exception ex);
        void onDelete(Exception ex);
    }

    public interface DocFilter {
        boolean filter(VKApiDocument doc);
    }

    private GetMyDocuments databaseInteractor;
    private LoadMyDocuments networkInteractor;
    private Subscriber<List<VKApiDocument>> databaseSubscriber = Subscribers.empty();
    private Subscriber<List<VKApiDocument>> networkSubscriber = Subscribers.empty();
    private DocFilter filter;

    private Callback callback;

    public CommonDocumentsPresenter(DocFilter filter, EventBus eventBus, DocumentRepository repository, Callback callback) {
        this.databaseInteractor = new GetMyDocuments(AndroidSchedulers.mainThread(), Schedulers.io(), eventBus, true, repository);
        this.networkInteractor = new LoadMyDocuments(AndroidSchedulers.mainThread(), Schedulers.io(), eventBus, true, repository);
        this.callback = callback;
    }

    public CommonDocumentsPresenter(DocFilter filter, EventBus eventBus, DocumentRepository repository) {
        this.filter = filter;
        this.databaseInteractor = new GetMyDocuments(AndroidSchedulers.mainThread(), Schedulers.io(), eventBus, true, repository);
        this.networkInteractor = new LoadMyDocuments(AndroidSchedulers.mainThread(), Schedulers.io(), eventBus, true, repository);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void makeOffline(VKApiDocument document) {

    }

    public void rename(VKApiDocument document, String newName) {

    }

    public void delete(VKApiDocument document) {

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
                callback.onGetDocuments(filterList(vkApiDocuments));
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
                callback.onGetDocuments(filterList(vkApiDocuments));
        }

        @Override
        public void onError(Throwable e) {
            if (callback != null)
                callback.onNetworkError((Exception) e);
        }
    }

    private List<VKApiDocument> filterList(List<VKApiDocument> list) {
        List<VKApiDocument> ret = new ArrayList<>();
        for (VKApiDocument x : list)
            if (filter.filter(x))
                ret.add(x);
        return ret;
    }
}