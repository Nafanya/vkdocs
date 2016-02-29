package io.github.nafanya.vkdocs.presentation.ui.views.docs;

import com.vk.sdk.api.model.VKApiDocument;

import io.github.nafanya.vkdocs.presentation.presenter.base.CommonDocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.ui.views.base.AbstractDocumentsListFragment;
import timber.log.Timber;

public class DefaultDocumentsListFragment extends AbstractDocumentsListFragment<CommonDocumentsPresenter> {

    public static DefaultDocumentsListFragment newInstance(CommonDocumentsPresenter presenter) {
        DefaultDocumentsListFragment fragment = new DefaultDocumentsListFragment();
        return AbstractDocumentsListFragment.newInstance(fragment, presenter);
    }

    @Override
    public void onClick(int position, VKApiDocument document) {
        Timber.d("ON CLICK ON POSITION " + position);
        //TODO open document here
    }
}
