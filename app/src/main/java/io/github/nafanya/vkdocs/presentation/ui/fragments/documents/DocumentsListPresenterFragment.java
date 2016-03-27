package io.github.nafanya.vkdocs.presentation.ui.fragments.documents;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;

import java.util.List;

import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.presenter.DocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.DocFilter;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.ExtDocFilter;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.OfflineDocFilter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.base.BaseSortedAdapter;
import io.github.nafanya.vkdocs.utils.FileFormatter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;

/**
 * Created by nafanya on 3/25/16.
 */
public abstract class DocumentsListPresenterFragment extends DocumentsListBaseFragment implements DocumentsPresenter.Callback {

    protected DocumentsPresenter presenter;
    protected BaseSortedAdapter adapter;
    protected FileFormatter fileFormatter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App app = (App)getActivity().getApplication();
        fileFormatter = app.getFileFormatter();
        presenter = new DocumentsPresenter(
                getFilter(),
                app.getEventBus(),
                app.getRepository(),
                app.getDownloadManager(),
                app.getOfflineManager(),
                app.getCacheManager(),
                (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE),
                this);
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
        presenter.getDocuments();
    }

    @Override
    public void onStop() {
        presenter.onStop();
        super.onStop();
    }

    protected abstract BaseSortedAdapter newAdapter();

    protected DocFilter getFilter() {
        if (!isOffline) {
            if (documentType == null) {
                return ExtDocFilter.ALL;
            }
            return new ExtDocFilter(documentType);
        } else {
            if (documentType == null) {
                return OfflineDocFilter.ALL;
            }
            return new OfflineDocFilter(documentType);
        }
    }

    private void updateData(List<VkDocument> documents) {
        if (adapter == null) {
            adapter = newAdapter();
        }
        adapter.setData(documents);
        adapter.setSearchFilter(searchQuery);
        adapter.setSortMode(sortMode);
        if (recyclerView.getAdapter() != adapter) {
            recyclerView.setAdapter(new SlideInBottomAnimationAdapter(adapter));
        }
    }

    @Override
    public void onGetDocuments(List<VkDocument> documents) {
        updateData(documents);
    }

    @Override
    public void onNetworkDocuments(List<VkDocument> documents) {
        updateData(documents);
        setRefresh(false);
    }

    @Override
    public void onNetworkError(Exception ex) {
        setRefresh(false);
        Snackbar snackbar = Snackbar
                .make(swipeRefreshLayout, R.string.no_internet_connection, Snackbar.LENGTH_LONG)
                .setAction(R.string.retry, view -> {
                    onRefresh();
                });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }
}
