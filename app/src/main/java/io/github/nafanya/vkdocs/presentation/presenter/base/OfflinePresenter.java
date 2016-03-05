package io.github.nafanya.vkdocs.presentation.presenter.base;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.List;

import io.github.nafanya.vkdocs.domain.download.DownloadRequest;
import io.github.nafanya.vkdocs.domain.download.base.DownloadManager;
import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.GetDownloadableDocuments;
import io.github.nafanya.vkdocs.domain.interactor.base.DefaultSubscriber;
import io.github.nafanya.vkdocs.domain.model.DownloadableDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class OfflinePresenter extends DocumentsPresenter {

    public interface Callback extends DocumentsPresenter.Callback {
        void onGetDownloadableDocuments(List<DownloadableDocument> downDocs);
    }

    private GetDownloadableDocuments downloadableDocumentsInteractor;
    private Callback downCallback;

    public OfflinePresenter(DocFilter filter, EventBus eventBus, DocumentRepository repository, DownloadManager<DownloadRequest> downloadManager, Callback callback) {
        super(filter, eventBus, repository, downloadManager, callback);
        this.downloadableDocumentsInteractor = new GetDownloadableDocuments(AndroidSchedulers.mainThread(), Schedulers.io(), eventBus, true, repository, downloadManager);
        downCallback = callback;
    }


    public void getDownloadableDocuments() {
    }


    public class NetworkSubscriber extends DefaultSubscriber<List<DownloadableDocument>> {
        //TODO fix it
        @Override
        public void onNext(List<DownloadableDocument> downDocs) {
            if (downCallback != null)
                downCallback.onGetDownloadableDocuments(downDocs);
                //callback.onGetDocuments(filterList(vkApiDocuments));
        }

        @Override
        public void onError(Throwable e) {
            if (downCallback != null)
                downCallback.onNetworkError((Exception) e);
        }
    }

}
