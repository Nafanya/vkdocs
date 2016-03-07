package io.github.nafanya.vkdocs.presentation.ui.views.mydocs.tabs;

import com.vk.sdk.api.model.VKApiDocument;

import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.MyDocsAdapter;
import io.github.nafanya.vkdocs.presentation.ui.views.base.AbstractMyDocsListFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.base.DocFilters;

public class GifsListFragment extends AbstractMyDocsListFragment<DocumentsPresenter, MyDocsAdapter> {

    @Override
    public DocumentsPresenter newPresenter() {
        return defaultPresenter(DocFilters.GIFS);
    }

    @Override
    public MyDocsAdapter newAdapter() {
        return defaultAdapter();
    }

    @Override
    public void onClick(int position, VKApiDocument document) {

    }
}
