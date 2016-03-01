package io.github.nafanya.vkdocs.presentation.presenter.base;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.List;

public class OfflinePresenter extends BasePresenter {
    public interface Callback {
        void onGetOfflineDocs(List<VKApiDocument> documents);
        void onDownloadFinished(VKApiDocument doc);
    }

    public void getOfflineDocs() {

    }
}
