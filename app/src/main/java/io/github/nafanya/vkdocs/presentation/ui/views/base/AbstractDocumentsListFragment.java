package io.github.nafanya.vkdocs.presentation.ui.views.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import io.github.nafanya.vkdocs.domain.download.InterruptableDownloadManager;
import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.DocumentsAdapter;
import timber.log.Timber;

public abstract class AbstractDocumentsListFragment<
        PresenterType extends DocumentsPresenter,
        AdapterType extends DocumentsAdapter> extends Fragment
        implements DocumentsPresenter.Callback, DocumentsAdapter.ItemEventListener {

    protected PresenterType presenter;
    protected AdapterType adapter;

    private InterruptableDownloadManager downloadManager;

    protected static String PRESENTER_KEY = "presenter";

    @Bind(R.id.list_documents)
    RecyclerView recyclerView;

    protected abstract PresenterType newPresenter();
    protected abstract AdapterType newAdapter();

    //temp helper
    protected DocumentsPresenter defaultPresenter(DocumentsPresenter.DocFilter filter) {
        App app = (App)getActivity().getApplication();
        return new DocumentsPresenter(filter, app.getEventBus(), app.getRepository(), this);

    }

    //temp helper
    protected DocumentsAdapter defaultAdapter() {
        return new DocumentsAdapter(this);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = newPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("ON CREATE VIEW");
        View rootView = inflater.inflate(R.layout.fragment_documents_list, container, false);
        Activity activity = getActivity();

        ButterKnife.bind(this, rootView);

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        downloadManager = ((App) activity.getApplication()).getDownloadManager();

        presenter.setCallback(this);
        presenter.loadDatabaseDocuments();

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void onDestroy() {
        Timber.d("ON DESTROY");
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        Timber.d("ON DESTROY VIEW");
        super.onDestroyView();
    }

    @Override
    public void onGetDocuments(List<VKApiDocument> documents) {
        Timber.d("ON GET DOCUMENTS");
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

    //method from adapter or context menu, uncomment when delete was implemented
    /*@Override
    public void onDeleteDocument(int position) {
        Timber.d("ON CLICK DELETE");
        new DeleteDocument(AndroidSchedulers.mainThread(), Schedulers.io(),
                eventBus, false, repository, adapter.getItem(position)).execute();
        adapter.removeIndex(position);
    }*/

    @Override
    public void onDatabaseError(Exception ex) {
        ex.printStackTrace();
        Timber.d("db error");
    }
}
