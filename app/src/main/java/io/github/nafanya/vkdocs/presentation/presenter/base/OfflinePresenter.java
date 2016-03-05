package io.github.nafanya.vkdocs.presentation.presenter.base;

import java.util.List;

import io.github.nafanya.vkdocs.domain.download.DownloadRequest;
import io.github.nafanya.vkdocs.domain.download.base.DownloadManager;
import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.GetDownloadableDocuments;
import io.github.nafanya.vkdocs.domain.interactor.base.DefaultSubscriber;
import io.github.nafanya.vkdocs.domain.model.DownloadableDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class OfflinePresenter extends DocumentsPresenter {

    public interface Callback extends DocumentsPresenter.Callback {
        void onGetDownloadableDocuments(List<DownloadableDocument> downDocs);
    }

    private GetDownloadableDocuments downloadableDocumentsInteractor;
    protected Subscriber<List<DownloadableDocument>> downloadableSubscriber;
    private Callback downloadableCallback;

    public OfflinePresenter(DocFilter filter, EventBus eventBus, DocumentRepository repository, DownloadManager<DownloadRequest> downloadManager, Callback callback) {
        super(filter, eventBus, repository, downloadManager, callback);
        this.downloadableDocumentsInteractor = new GetDownloadableDocuments(AndroidSchedulers.mainThread(), Schedulers.io(), eventBus, true, repository, downloadManager);
        downloadableCallback = callback;
    }


    public void getDownloadableDocuments() {
        downloadableSubscriber = new DownloadableSubscriber();
        downloadableDocumentsInteractor.execute(downloadableSubscriber);
    }


    public class DownloadableSubscriber extends DefaultSubscriber<List<DownloadableDocument>> {
        //TODO fix it
        @Override
        public void onNext(List<DownloadableDocument> downDocs) {
            if (downloadableCallback != null)
                downloadableCallback.onGetDownloadableDocuments(downDocs);
                //callback.onGetDocuments(filterList(vkApiDocuments));
        }

        @Override
        public void onError(Throwable e) {
            if (downloadableCallback != null)
                downloadableCallback.onNetworkError((Exception) e);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        downloadableSubscriber.unsubscribe();
    }
}
