package io.github.nafanya.vkdocs.presentation.ui.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.DocFilter;
import io.github.nafanya.vkdocs.presentation.ui.SortMode;
import io.github.nafanya.vkdocs.presentation.ui.adapters.base.CommonItemEventListener;
import io.github.nafanya.vkdocs.presentation.ui.decorators.EndOffsetItemDecorator;
import io.github.nafanya.vkdocs.presentation.ui.decorators.SimpleDivierItermDecorator;

/**
 * Created by nafanya on 3/15/16.
 */
public abstract class BaseDocumentsFragment extends Fragment implements DocumentsPresenter.Callback {

    public interface Callback extends CommonItemEventListener {
        //TODO holy shit, copypaste from document presenter
        void onNetworkError(Exception ex);
        void onDatabaseError(Exception ex);
        void onMakeOffline(Exception ex);
        void onRename(Exception ex);
        void onDelete(Exception ex);

        void onOpenDocument(VkDocument document);
        void onAlreadyDownloading(VkDocument document, boolean isRealyAlreadyDownloading);
        void onNoInternetWhenOpen();
    }

    public static final String ARG_DOC_TYPE = "arg_doc_type";
    public static final String ARG_SORT_MODE = "arg_sort_mode";

    protected SortMode sortMode;
    protected DocFilter filter;
    protected DocumentsPresenter presenter;

    @Bind(R.id.list_documents) RecyclerView recyclerView;

    public BaseDocumentsFragment() {
        // Required empty public constructor
    }

    protected Callback listener;
    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        try {
            listener = (Callback)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Callback");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        filter = (DocFilter) getArguments().get(ARG_DOC_TYPE);
        sortMode = (SortMode) getArguments().get(ARG_SORT_MODE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_documents_list, container, false);

        ButterKnife.bind(this, root);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new SimpleDivierItermDecorator(getActivity()));
        // Convert dp to px
        final int px = (int) (getActivity().getResources().getDimension(R.dimen.recyclerview_bottom_padding) * getActivity().getResources().getDisplayMetrics().density);
        recyclerView.addItemDecoration(new EndOffsetItemDecorator(px));

        return root;
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

    @Override
    public void onMakeOffline(Exception ex) {
        listener.onMakeOffline(ex);
    }

    @Override
    public void onRename(Exception ex) {
        listener.onRename(ex);
    }

    @Override
    public void onDelete(Exception ex) {
        listener.onDelete(ex);
    }

    @Override
    public void onNetworkError(Exception ex) {
        listener.onNetworkError(ex);
    }

    @Override
    public void onDatabaseError(Exception ex) {
        listener.onDatabaseError(ex);
    }

    @Override
    public void onOpenFile(VkDocument document) {
        listener.onOpenDocument(document);
    }

    @Override
    public void onNoInternetWhenOpen() {
        listener.onNoInternetWhenOpen();
    }

    @Override
    public void onAlreadyDownloading(VkDocument document, boolean isReallyAlreadyDownloading) {
        listener.onAlreadyDownloading(document, isReallyAlreadyDownloading);
    }

    public DocumentsPresenter presenter() {
        return presenter;
    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.documents_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        super.onCreateOptionsMenu(menu, inflater);
    }*/
}
