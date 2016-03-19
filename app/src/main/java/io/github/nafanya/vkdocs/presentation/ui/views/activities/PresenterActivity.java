package io.github.nafanya.vkdocs.presentation.ui.views.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.List;

import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.DocFilter;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.ExtDocFilter;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.OfflineDocFilter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.base.BaseSortedAdapter;
import io.github.nafanya.vkdocs.utils.FileFormatter;
import timber.log.Timber;

/**
 * Created by pva701 on 15.03.16.
 */
public abstract class PresenterActivity extends BaseActivity implements DocumentsPresenter.Callback {

    protected DocumentsPresenter presenter;
    protected BaseSortedAdapter adapter;
    protected FileFormatter fileFormatter;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        App app = (App)getApplication();
        fileFormatter = app.getFileFormatter();

        presenter = new DocumentsPresenter(
                getFilter(navDrawerPos, extType),
                app.getEventBus(),
                app.getRepository(),
                app.getDownloadManager(),
                app.getOfflineManager(),
                app.getInternetService(),
                app.getAppCacheRoot(),
                app.getAppOfflineRoot(),
                this);
        presenter.getDocuments();
    }

    protected DocFilter getFilter(int section, VkDocument.ExtType extType) {
        if (section == 1) {
            if (extType == null)
                return ExtDocFilter.ALL;
            return new ExtDocFilter(extType);
        } else {
            if (extType == null)
                return OfflineDocFilter.ALL;
            return new OfflineDocFilter(extType);
        }
    }

    protected abstract BaseSortedAdapter newAdapter();

    private void updateData(List<VkDocument> documents) {
        if (adapter == null)
            adapter = newAdapter();
        adapter.setData(documents);
        if (recyclerView.getAdapter() != adapter)
            recyclerView.setAdapter(adapter);
    }

    @Override
    public void onGetDocuments(List<VkDocument> documents) {
        Timber.d("ON GET DOCUMENTS");
        updateData(documents);
    }

    @Override
    public void onNetworkDocuments(List<VkDocument> documents) {
        Timber.d("ON NETWORK DOCUMENTS");
        setRefresh(false);
        updateData(documents);
    }

    @Override
    public void onNetworkError(Exception ex) {
        setRefresh(false);
        Snackbar snackbar = Snackbar
                .make(cooridnatorLayout, "No internet connection", Snackbar.LENGTH_LONG)
                .setAction("RETRY", view -> {
                    Timber.d("retry refresh");
                    onRefresh();
                });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    @Override
    public void onDatabaseError(Exception ex) {

    }

    protected void openDocument(VkDocument document) {
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String mimeType = myMime.getMimeTypeFromExtension(document.getExt());
        Timber.d("[openDocument] path = %s", document.getPath());
        File fileDoc = new File(document.getPath());
        newIntent.setDataAndType(Uri.fromFile(fileDoc), mimeType);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//TODO one task?
        try {
            startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            //TODO do something
            //Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }
    }
}
