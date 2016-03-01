package io.github.nafanya.vkdocs.presentation.ui.views.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import io.github.nafanya.vkdocs.presentation.presenter.base.BasePresenter;
import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.MyDocsAdapter;

public abstract class AbstractListFragment<
        PresenterType extends BasePresenter,
        AdapterType> extends Fragment {

    protected PresenterType presenter;
    protected AdapterType adapter;

    protected abstract PresenterType newPresenter();
    protected abstract AdapterType newAdapter();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = newPresenter();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    //method from adapter or context menu, uncomment when delete was implemented
    /*@Override
    public void onDeleteDocument(int position) {
        Timber.d("ON CLICK DELETE");
        new DeleteDocument(AndroidSchedulers.mainThread(), Schedulers.io(),
                eventBus, false, repository, adapter.getItem(position)).execute();
        adapter.removeIndex(position);
    }*/
}
