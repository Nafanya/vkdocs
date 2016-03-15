package io.github.nafanya.vkdocs.presentation.ui.views.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.List;

import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.DocFilter;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.ExtDocFilter;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.OfflineDocFilter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.DocumentsAdapter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.OfflineAdapter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.base.AbstractAdapter;
import io.github.nafanya.vkdocs.presentation.ui.views.dialogs.OpenProgressDialog;
import io.github.nafanya.vkdocs.utils.FileFormatter;
import timber.log.Timber;

/**
 * Created by pva701 on 15.03.16.
 */
public abstract class PresenterActivity extends BaseActivity implements
        DocumentsPresenter.Callback,
        OfflineAdapter.ItemEventListener {

    protected DocumentsPresenter presenter;
    protected AbstractAdapter adapter;
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
                app.getInternetService(),
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

    protected AbstractAdapter newAdapter(int section) {
        if (section == 1)
            return new DocumentsAdapter(this, fileFormatter, sortMode, this);
        else
            return new OfflineAdapter(this, fileFormatter, sortMode, this);
    }

    @Override
    public void onGetDocuments(List<VkDocument> documents) {
        if (adapter == null)
            adapter = newAdapter(navDrawerPos);
        adapter.setData(documents);
        if (recyclerView.getAdapter() == null)
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
    public void onOpenDocument(VkDocument document) {
        openDocument(document);
    }

    @Override
    public void onAlreadyDownloading(VkDocument document, boolean isReallyAlreadyDownloading) {
        DialogFragment fragment = OpenProgressDialog.newInstance(document, isReallyAlreadyDownloading);
        fragment.show(getSupportFragmentManager(), "progress_open");
    }

    @Override
    public void onNoInternetWhenOpen() {

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
