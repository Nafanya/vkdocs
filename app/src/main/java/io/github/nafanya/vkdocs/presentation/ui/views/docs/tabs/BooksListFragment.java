package io.github.nafanya.vkdocs.presentation.ui.views.docs.tabs;

import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.DocumentsAdapter;
import io.github.nafanya.vkdocs.presentation.ui.views.base.BlockedOpeningListFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.base.DocFilters;


public class BooksListFragment extends BlockedOpeningListFragment<DocumentsPresenter, DocumentsAdapter> {

    @Override
    public DocumentsPresenter newPresenter() {
        return defaultPresenter(DocFilters.BOOKS);
    }

    @Override
    public DocumentsAdapter newAdapter() {
        return defaultAdapter();
    }
}
