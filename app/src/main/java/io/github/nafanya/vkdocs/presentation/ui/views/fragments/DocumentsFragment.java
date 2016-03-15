package io.github.nafanya.vkdocs.presentation.ui.views.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.List;

import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.ExtDocFilter;
import io.github.nafanya.vkdocs.presentation.ui.SortMode;
import io.github.nafanya.vkdocs.presentation.ui.adapters.DocumentsAdapter;

/**
 * Implementation of {@link DocumentsPresenter} methods.
 */
public class DocumentsFragment extends BaseDocumentsFragment {

    public static DocumentsFragment newInstance(VkDocument.ExtType type, @NonNull SortMode sortMode) {
        Bundle bundle = new Bundle();
        if (type == null) {
            bundle.putSerializable(ARG_DOC_TYPE, ExtDocFilter.ALL);
        } else {
            bundle.putSerializable(ARG_DOC_TYPE, new ExtDocFilter(type));
        }
        bundle.putSerializable(ARG_SORT_MODE, sortMode);
        DocumentsFragment fragment = new DocumentsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private DocumentsAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App app = (App) getActivity().getApplication();
        presenter = new DocumentsPresenter(
                filter,
                app.getEventBus(),
                app.getRepository(),
                app.getDownloadManager(),
                app.getInternetService(),
                this);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter.setCallback(this);
        presenter.getDocuments();
    }

    @Override
    public void onGetDocuments(List<VkDocument> documents) {
        if (adapter == null) {
            App app = (App)getActivity().getApplication();
            adapter = new DocumentsAdapter(getActivity(), app.getFileFormatter(), sortMode, listener);
        }
        adapter.setData(documents);
        if (recyclerView.getAdapter() == null)
            recyclerView.setAdapter(adapter);
    }
}
