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
import io.github.nafanya.vkdocs.presentation.presenter.base.CommonDocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.DocumentsAdapter;
import timber.log.Timber;

public abstract class AbstractDocumentsListFragment<T extends CommonDocumentsPresenter> extends Fragment
        implements CommonDocumentsPresenter.Callback, DocumentsAdapter.ItemEventListener {

    protected T presenter;

    protected DocumentsAdapter adapter;

    private InterruptableDownloadManager downloadManager;

    protected static String PRESENTER_KEY = "presenter";
    //private static String ADAPTER_KEY = "adapter";

    @Bind(R.id.list_documents)
    RecyclerView recyclerView;

    public static <R extends AbstractDocumentsListFragment> R newInstance(R fragment, CommonDocumentsPresenter presenter) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PRESENTER_KEY, presenter);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = (T)getArguments().get(PRESENTER_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("ON CREATE VIEW");
        View rootView = inflater.inflate(R.layout.fragment_documents_list, container, false);
        Activity activity = getActivity();

        ButterKnife.bind(this, rootView);

        activity.setTitle(R.string.title_activity_documents);
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
        //if (adapter == null) {
            Timber.d("on get documents");
            adapter = new DocumentsAdapter(this);

        //}
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
