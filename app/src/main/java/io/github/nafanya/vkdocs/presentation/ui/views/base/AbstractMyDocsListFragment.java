package io.github.nafanya.vkdocs.presentation.ui.views.base;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.MyDocsAdapter;
import timber.log.Timber;

public abstract class AbstractMyDocsListFragment<
        PresenterType extends DocumentsPresenter,
        AdapterType extends MyDocsAdapter> extends AbstractListFragment<PresenterType, AdapterType>
        implements DocumentsPresenter.Callback, MyDocsAdapter.ItemEventListener {

    @Bind(R.id.list_documents)
    RecyclerView recyclerView;

    //temp helper
    protected DocumentsPresenter defaultPresenter(DocumentsPresenter.DocFilter filter) {
        App app = (App)getActivity().getApplication();
        return new DocumentsPresenter(filter, app.getEventBus(), app.getRepository(), app.getDownloadManager(), this);
    }

    //temp helper
    protected MyDocsAdapter defaultAdapter() {
        return new MyDocsAdapter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_documents_list, container, false);

        ButterKnife.bind(this, rootView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        presenter.setCallback(this);
        presenter.loadDatabaseDocuments();

        return rootView;
    }

    @Override
    public void onGetDocuments(List<VkDocument> documents) {
        adapter = newAdapter();
        adapter.setData(documents);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onMakeOffline(Exception ex) {
        //TODO write here
    }

    @Override
    public void onRename(Exception ex) {
        //TODO write here
    }

    @Override
    public void onDelete(Exception ex) {
        //TODO write here
    }

    @Override
    public void onNetworkError(Exception ex) {
        Timber.d("network error");
    }

    @Override
    public void onDatabaseError(Exception ex) {
        Timber.d("db error");
    }
}
