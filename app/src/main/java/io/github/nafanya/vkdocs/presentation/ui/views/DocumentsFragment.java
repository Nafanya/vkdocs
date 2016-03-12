package io.github.nafanya.vkdocs.presentation.ui.views;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.ExtDocFilter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.MyDocsAdapter;
import io.github.nafanya.vkdocs.presentation.ui.decorators.SimpleDivierItermDecorator;

/**
 * A simple {@link Fragment} subclass.
 */
public class DocumentsFragment extends Fragment implements DocumentsPresenter.Callback, MyDocsAdapter.ItemEventListener {

    public static final String ARG_DOC_TYPE = "arg_doc_type";

    private DocFilter filter;

    @Bind(R.id.list_documents) RecyclerView recyclerView;
    private MyDocsAdapter adapter;
    private DocumentsPresenter presenter;

    public DocumentsFragment() {
        // Required empty public constructor
    }

    public static DocumentsFragment newInstance(VkDocument.ExtType type) {
        Bundle bundle = new Bundle();
        if (type == null) {
            bundle.putSerializable(ARG_DOC_TYPE, ExtDocFilter.ALL);
        } else {
            bundle.putSerializable(ARG_DOC_TYPE, new ExtDocFilter(type));
        }
        DocumentsFragment fragment = new DocumentsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App app = (App) getActivity().getApplication();
        DocFilter filter = (DocFilter) getArguments().get(ARG_DOC_TYPE);
        presenter = new DocumentsPresenter(
                filter,
                app.getEventBus(),
                app.getRepository(),
                app.getDownloadManager(),
                app.getInternetService(),
                this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_documents_list, container, false);

        ButterKnife.bind(this, root);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new SimpleDivierItermDecorator(getActivity()));

        presenter.setCallback(this);
        presenter.getDocuments();

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
    public void onGetDocuments(List<VkDocument> documents) {
        if (adapter == null) {
            App app = (App)getActivity().getApplication();
            adapter = new MyDocsAdapter(app.getFileFormatter(), app.getDocIcons(), this);
        }
        adapter.setData(documents);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onNetworkError(Exception ex) {

    }

    @Override
    public void onDatabaseError(Exception ex) {

    }

    @Override
    public void onMakeOffline(Exception ex) {

    }

    @Override
    public void onRename(Exception ex) {

    }

    @Override
    public void onDelete(Exception ex) {

    }

    @Override
    public void onOpenFile(VkDocument document) {

    }

    @Override
    public void onDownloadingFile(VkDocument document) {

    }

    @Override
    public void onNoInternetWhenOpen() {

    }

    @Override
    public void onClick(int position, VkDocument document) {

    }

    @Override
    public void onClickContextMenu(int position, VkDocument document) {

    }

    @Override
    public void onClickMakeOffline(int position, VkDocument document) {

    }
}
