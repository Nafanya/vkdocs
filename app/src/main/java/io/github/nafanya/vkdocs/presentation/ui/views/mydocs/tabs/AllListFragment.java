package io.github.nafanya.vkdocs.presentation.ui.views.mydocs.tabs;

import com.vk.sdk.api.model.VKApiDocument;

import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.MyDocsAdapter;
import io.github.nafanya.vkdocs.presentation.ui.views.base.AbstractMyDocsListFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.base.DocFilters;


public class AllListFragment extends AbstractMyDocsListFragment<DocumentsPresenter, MyDocsAdapter> {
    @Override
    public DocumentsPresenter newPresenter() {
        return defaultPresenter(DocFilters.ALL);
    }

    @Override
    public MyDocsAdapter newAdapter() {
        return new MyDocsAdapter(this);
    }

    @Override
    public void onClick(int position, VKApiDocument document) {

    }
}

