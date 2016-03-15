package io.github.nafanya.vkdocs.presentation.ui.views.documents;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import java.util.List;

import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.MyDocsAdapter;

/**
 * Implementation of {@link DocumentsPresenter} methods.
 */
public class DocumentsFragment extends BaseDocumentsFragment implements DocumentsPresenter.Callback {

    private DocumentsPresenter presenter;

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
            adapter = new MyDocsAdapter(getActivity(), app.getFileFormatter(), sortMode, this);
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
    public void onAlreadyDownloading(VkDocument document, boolean isRealyAlreadyDownloading) {

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

}
