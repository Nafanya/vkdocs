package io.github.nafanya.vkdocs.presentation.ui.views;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.DocFilter;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.OfflineDocFilter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.OfflineAdapter;
import io.github.nafanya.vkdocs.presentation.ui.views.base.AbstractListFragment;
import timber.log.Timber;

public class OfflineListFragment
        extends AbstractListFragment<OfflineAdapter>
        implements DocumentsPresenter.Callback, OfflineAdapter.ItemEventListener  {

    public static DocFilter ALL_OFFLINE = (DocFilter) doc -> doc.isOffline() || doc.isOfflineInProgress();

    public static OfflineListFragment newInstance(VkDocument.ExtType type) {
        Bundle bundle = new Bundle();
        if (type == null)
            bundle.putSerializable(EXT_TYPE_KEY, ALL_OFFLINE);
        else
            bundle.putSerializable(EXT_TYPE_KEY, new OfflineDocFilter(type));
        OfflineListFragment fragment = new OfflineListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Bind(R.id.list_documents)
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_documents_list, container, false);

        ButterKnife.bind(this, rootView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        presenter.setCallback(this);
        presenter.getDocuments();

        return rootView;
    }

    @Override
    public void onGetDocuments(List<VkDocument> documents) {
        Timber.d("offline adapter = " + documents.size());
        if (adapter == null)
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
    public void onCancelDownloading(int position, VkDocument document) {
        presenter.cancelDownloading(document);
        adapter.removeIndex(position);
    }

    @Override
    public void onNetworkError(Exception ex) {
        Timber.d("network error");
    }

    @Override
    public void onDatabaseError(Exception ex) {
        Timber.d("db error");
    }

    public OfflineAdapter newAdapter() {
        App app = (App)getActivity().getApplication();
        return new OfflineAdapter(app.getFileFormatter(), this);
    }
}
