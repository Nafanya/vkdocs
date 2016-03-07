package io.github.nafanya.vkdocs.presentation.ui.views.mydocs.tabs;

import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.MyDocsAdapter;
import io.github.nafanya.vkdocs.presentation.ui.views.base.BlockedOpeningListFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.base.DocFilters;


public class ArchivesListFragment extends BlockedOpeningListFragment<DocumentsPresenter, MyDocsAdapter> {
    @Override
    public DocumentsPresenter newPresenter() {
        return defaultPresenter(DocFilters.TEXT);
    }

    @Override
    public MyDocsAdapter newAdapter() {
        return defaultAdapter();
    }
}
