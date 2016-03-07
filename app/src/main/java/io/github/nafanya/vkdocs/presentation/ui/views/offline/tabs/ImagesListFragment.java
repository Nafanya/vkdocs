package io.github.nafanya.vkdocs.presentation.ui.views.offline.tabs;

import com.vk.sdk.api.model.VKApiDocument;

import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.MyDocsAdapter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.OfflineAdapter;
import io.github.nafanya.vkdocs.presentation.ui.views.base.AbstractMyDocsListFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.base.AbstractOfflineListFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.base.DocFilters;

public class ImagesListFragment extends AbstractOfflineListFragment<DocumentsPresenter, OfflineAdapter> {


    @Override
    public DocumentsPresenter newPresenter() {
        return defaultPresenter(DocFilters.IMAGES);
    }

    @Override
    public OfflineAdapter newAdapter() {
        return defaultAdapter();
    }

    @Override
    public void onClick(int position, VKApiDocument document) {

    }
}
