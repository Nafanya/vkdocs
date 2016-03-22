package io.github.nafanya.vkdocs.presentation.ui.views.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.DialogFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.SortMode;
import io.github.nafanya.vkdocs.presentation.ui.adapters.DocumentsAdapter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.OfflineAdapter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.base.BaseSortedAdapter;
import io.github.nafanya.vkdocs.presentation.ui.views.dialogs.BottomMenu;
import io.github.nafanya.vkdocs.presentation.ui.views.dialogs.DeleteDialog;
import io.github.nafanya.vkdocs.presentation.ui.views.dialogs.ErrorOpenDialog;
import io.github.nafanya.vkdocs.presentation.ui.views.dialogs.OpenProgressDialog;
import io.github.nafanya.vkdocs.presentation.ui.views.dialogs.RenameDialog;
import io.github.nafanya.vkdocs.utils.FileFormatter;
import timber.log.Timber;

/**
 * Created by pva701 on 15.03.16.
 */
public class DocumentsActivity extends PresenterActivity implements
        BottomMenu.MenuEventListener,
        OpenProgressDialog.Callback,
        ErrorOpenDialog.Callback,
        OfflineAdapter.ItemEventListener,
        RenameDialog.Callback,
        DeleteDialog.Callback {
    private static String CONTEXT_DOC_KEY = "context_doc_key";
    private static String CONTEXT_POS_KEY = "context_pos_key";

    private BottomSheetDialog dialog;
    private VkDocument restoreContextMenuDoc;
    private int restoreDocPosition;
    private FileFormatter fileFormatter;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        App app = (App)getApplication();
        fileFormatter = app.getFileFormatter();
        if (state != null) {
            restoreContextMenuDoc = state.getParcelable(CONTEXT_DOC_KEY);
            restoreDocPosition = state.getInt(CONTEXT_POS_KEY);
        }
        if (restoreContextMenuDoc != null)
            onClickContextMenu(restoreDocPosition, restoreContextMenuDoc);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putParcelable(CONTEXT_DOC_KEY, restoreContextMenuDoc);
        state.putInt(CONTEXT_POS_KEY, restoreDocPosition);
        super.onSaveInstanceState(state);
    }

    @Override
    public void onRefresh() {
        Timber.d("ON REFRESH");
        setRefresh(true);
        presenter.forceNetworkLoad();
    }

    /***BaseActivity overrides***/
    @Override
    public void onTypeFilterChanged(VkDocument.ExtType extType) {
        presenter.setFilter(getFilter(navDrawerPos, extType));
        presenter.getDocuments();
    }

    @Override
    public void onSortModeChanged(SortMode sortMode) {
        super.onSortModeChanged(sortMode);
        adapter.setSortMode(sortMode);
    }

    @Override
    public void onSectionChanged(int newSection) {
        presenter.setFilter(getFilter(newSection, extType));
        if (adapter != null)
            adapter.removeData();
        adapter = null;
        if (recyclerView != null)
            recyclerView.scrollToPosition(0);
        presenter.getDocuments();
    }

    @Override
    public boolean onQueryTextChange(String query) {
        adapter.setSearchFilter(query);

        return super.onQueryTextChange(query);
    }

    @Override
    protected BaseSortedAdapter newAdapter() {
        if (navDrawerPos == 1)
            return new DocumentsAdapter(this, fileFormatter, sortMode, this);
        else
            return new OfflineAdapter(this, fileFormatter, sortMode, this);
    }

    /***Adapter callback***/
    @Override
    public void onClick(int position, VkDocument document) {
        if (document.getExtType() != VkDocument.ExtType.AUDIO &&
                document.getExtType() != VkDocument.ExtType.VIDEO &&
                document.getExtType() != VkDocument.ExtType.IMAGE &&
                document.getExtType() != VkDocument.ExtType.GIF) {
            Timber.d("on click: %s, offtype = %d, request %s", document.title, document.getOfflineType(), document.getRequest());
            Timber.d("is off %b, is ic_cached_green %b", document.isOffline(), document.isCached());
            presenter.openDocument(document);
        } else {
            Intent intent = new Intent(this, DocumentViewerActivity.class);
            intent.putParcelableArrayListExtra(DocumentViewerActivity.DOCUMENTS_KEY, (ArrayList<VkDocument>)adapter.getData());
            intent.putExtra(DocumentViewerActivity.POSITION_KEY, position);
            startActivity(intent);
        }
    }

    @Override
    public void onClickContextMenu(int position, VkDocument document) {
        restoreContextMenuDoc = document;
        this.restoreDocPosition = position;
        dialog = new BottomMenu(this, position, document, fileFormatter, this);
        dialog.show();
    }


    /***Offline adapter callbacks***/
    @Override
    public void onCancelDownloading(int position, VkDocument document) {
        presenter.cancelDownloading(document);
        adapter.removeIndex(position);
    }

    @Override
    public void onCompleteDownloading(int position, VkDocument document) {
        presenter.updateDocument(document);
        adapter.notifyItemChanged(position);
    }

    /***BottomMenu callbacks***/
    @Override
    public void onClickMakeOffline(int position, VkDocument document, boolean isMakeOffline) {
        Timber.d("[Bottom menu] clicked offline button for doc (title=%s, isMakeOffline=%s)", document.title, isMakeOffline);
        if (isMakeOffline) {
            presenter.makeOffline(document);
        }
    }

    @Override
    public void onClickRename(int position, VkDocument document) {
        dismissContextMenu();
        DialogFragment fragment = RenameDialog.newInstance(position, document);
        fragment.show(getSupportFragmentManager(), "rename");
    }

    @Override
    public void onClickDelete(int position, VkDocument document) {
        dismissContextMenu();
        DialogFragment fragment = DeleteDialog.newInstance(position, document);
        fragment.show(getSupportFragmentManager(), "delete");
    }

    @Override
    public void onClickShare(VkDocument document) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        Uri uri;
        if (document.isCached() || document.isOffline()) {
            uri = Uri.fromFile(new File(document.getPath()));
        } else {
            uri = Uri.parse(document.url);
        }
        Timber.d("[share] uri: %s", uri);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("*/*");

        PackageManager pm = getPackageManager();
        List<ResolveInfo> resInfo = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo info : resInfo) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            String packageName = info.activityInfo.packageName;
            if (packageName.contains("com.vkontakte.android")) {
                Timber.d("[share] uri: found VK app, using it");
                intent.setPackage(packageName);
                break;
            }
        }
        startActivity(intent);
    }

    @Override
    public void onCloseContextMenu() {
        dismissContextMenu();
    }

    @Override
    public void onClickDownload(int position, VkDocument document) {
        if (isStoragePermissionGranted()) {
            presenter.downloadDocumentToDownloads(document);
        }
    }

    public void dismissContextMenu() {
        dialog.dismiss();
        dialog = null;
        restoreContextMenuDoc = null;
        restoreDocPosition = -1;
    }

    /***Presenter callback for open document***/
    @Override
    public void onOpenDocument(VkDocument document) {
        openDocument(document);
    }

    @Override
    public void onAlreadyDownloading(VkDocument document, boolean isReallyAlreadyDownloading) {
        if (isReallyAlreadyDownloading)
            Timber.d("%s is already downloading now", document.title);
        else
            Timber.d("%s isn't downloading now yet", document.title);

        DialogFragment fragment = OpenProgressDialog.newInstance(document, isReallyAlreadyDownloading);
        fragment.show(getSupportFragmentManager(), "progress_open");
    }

    @Override
    public void onTriggeredOffline(VkDocument document) {
        adapter.notifyItemChanged(adapter.getData().indexOf(document));
    }

    /***OpenProgressDialog callbacks***/
    @Override
    public void onCancelCaching(VkDocument document, boolean isAlreadyDownloading) {
        if (!isAlreadyDownloading)
            presenter.cancelDownloading(document);
    }

    @Override
    public void onCompleteCaching(VkDocument document) {
        openDocument(document);
    }

    @Override
    public void onErrorCaching(Exception error, VkDocument document, boolean isAlreadyDownloading) {
        DialogFragment fragment = ErrorOpenDialog.newInstance(document, isAlreadyDownloading);
        fragment.show(getSupportFragmentManager(), "error_open");
    }

    /***ErrorOpen dialog callbacks***/
    @Override
    public void onRetry(VkDocument document, boolean isAlreadyDownloading) {
        presenter.retryDownloadDocument(document);
    }

    @Override
    public void onCancel(VkDocument document, boolean isAlreadyDownloading) {
        if (!isAlreadyDownloading)
            presenter.cancelDownloading(document);
    }

    /***Rename dialog callbacks***/
    @Override
    public void onCancelRename(VkDocument document) {

    }

    @Override
    public void onRename(int position, VkDocument document, String newName) {
        Timber.d("[On rename] %s newName %s", document.title, newName);
        presenter.rename(document, newName);
        adapter.notifyItemChanged(position);
    }

    /***Delete dialog callbacks***/
    @Override
    public void onCancelDelete(VkDocument document) {

    }

    @Override
    public void onDelete(int position, VkDocument document) {
        Timber.d("[On delete] %s", document.title);
        presenter.delete(document);
        adapter.removeIndex(position);
    }
}
