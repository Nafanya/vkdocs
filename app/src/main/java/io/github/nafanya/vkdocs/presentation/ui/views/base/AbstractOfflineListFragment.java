package io.github.nafanya.vkdocs.presentation.ui.views.base;


import io.github.nafanya.vkdocs.presentation.presenter.base.OfflinePresenter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.OfflineAdapter;

public abstract class AbstractOfflineListFragment<
        PresenterType extends OfflinePresenter,
        AdapterType extends OfflineAdapter>
        extends AbstractListFragment<PresenterType, AdapterType> implements OfflinePresenter.Callback  {
}
