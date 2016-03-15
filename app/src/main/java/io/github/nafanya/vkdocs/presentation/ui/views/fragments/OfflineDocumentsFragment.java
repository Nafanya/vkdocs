package io.github.nafanya.vkdocs.presentation.ui.views.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.List;

import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.DocFilter;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.OfflineDocFilter;
import io.github.nafanya.vkdocs.presentation.ui.SortMode;
import io.github.nafanya.vkdocs.presentation.ui.adapters.OfflineAdapter;
import timber.log.Timber;

public class OfflineDocumentsFragment extends BaseDocumentsFragment implements OfflineAdapter.ItemEventListener  {

    public static DocFilter ALL_OFFLINE = (DocFilter) doc -> doc.isOffline() || doc.isOfflineInProgress();

    public static DocumentsFragment newInstance(VkDocument.ExtType type, @NonNull SortMode sortMode) {
        Bundle bundle = new Bundle();
        if (type == null) {
            bundle.putSerializable(ARG_DOC_TYPE, ALL_OFFLINE);
        } else {
            bundle.putSerializable(ARG_DOC_TYPE, new OfflineDocFilter(type));
        }
        bundle.putSerializable(ARG_SORT_MODE, sortMode);
        DocumentsFragment fragment = new DocumentsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private OfflineAdapter adapter;

    @Override
    public void onGetDocuments(List<VkDocument> documents) {
        Timber.d("offline adapter = " + documents.size());
        if (adapter == null)
            adapter = newAdapter();
        adapter.setData(documents);
        if (recyclerView.getAdapter() == null)
            recyclerView.setAdapter(adapter);
    }

    public OfflineAdapter newAdapter() {
        App app = (App)getActivity().getApplication();
        return new OfflineAdapter(app.getFileFormatter(), this);
    }

    /***Adapter callbacks***/
    @Override
    public void onClick(int position, VkDocument document) {
        listener.onClick(position, document);
    }

    @Override
    public void onClickContextMenu(int position, VkDocument document) {
        listener.onClick(position, document);
    }

    @Override
    public void onCancelDownloading(int position, VkDocument document) {
        presenter.cancelDownloading(document);
        adapter.removeIndex(position);
    }
}
