package io.github.nafanya.vkdocs.presentation.ui.views.base;

import com.vk.sdk.api.model.VKApiDocument;

import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.DocumentsAdapter;
import timber.log.Timber;

public abstract class BlockedOpeningListFragment<T extends DocumentsPresenter, A extends DocumentsAdapter> extends AbstractDocumentsListFragment<T, A> {
    @Override
    public void onClick(int position, VKApiDocument document) {
        Timber.d("ON CLICK ITEM pos = " + position + ", this = " + this);
        //TODO implement here
    }
}
