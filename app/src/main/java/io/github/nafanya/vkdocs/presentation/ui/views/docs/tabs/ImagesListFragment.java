package io.github.nafanya.vkdocs.presentation.ui.views.docs.tabs;

import com.vk.sdk.api.model.VKApiDocument;

import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.DocumentsAdapter;
import io.github.nafanya.vkdocs.presentation.ui.views.base.AbstractDocumentsListFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.base.DocFilters;

public class ImagesListFragment extends AbstractDocumentsListFragment<DocumentsPresenter, DocumentsAdapter> {


    @Override
    public DocumentsPresenter newPresenter() {
        return defaultPresenter(DocFilters.IMAGES);
    }

    @Override
    public DocumentsAdapter newAdapter() {
        return new DocumentsAdapter(this);
    }

    @Override
    public void onClick(int position, VKApiDocument document) {

    }
}
