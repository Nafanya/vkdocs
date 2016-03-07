package io.github.nafanya.vkdocs.presentation.ui.views.offline.tabs;

import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.MyDocsAdapter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.OfflineAdapter;
import io.github.nafanya.vkdocs.presentation.ui.views.base.AbstractOfflineListFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.base.BlockedOpeningListFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.base.DocFilters;

public class TextListFragment extends AbstractOfflineListFragment<DocumentsPresenter, OfflineAdapter> {

    @Override
    public DocumentsPresenter newPresenter() {
        return defaultPresenter(DocFilters.TEXT);
    }

    @Override
    public OfflineAdapter newAdapter() {
        return defaultAdapter();
    }
}
