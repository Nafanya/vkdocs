package io.github.nafanya.vkdocs.presentation.ui.fragments.documents;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.DialogFragment;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.SortMode;
import io.github.nafanya.vkdocs.presentation.ui.activities.DocumentViewerActivity;
import io.github.nafanya.vkdocs.presentation.ui.adapters.DocumentsAdapter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.OfflineAdapter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.base.BaseSortedAdapter;
import io.github.nafanya.vkdocs.presentation.ui.dialogs.BottomMenu;
import io.github.nafanya.vkdocs.presentation.ui.dialogs.DeleteDialog;
import io.github.nafanya.vkdocs.presentation.ui.dialogs.ErrorOpenDialog;
import io.github.nafanya.vkdocs.presentation.ui.dialogs.RenameDialog;
import io.github.nafanya.vkdocs.utils.FileFormatter;
import timber.log.Timber;

/**
 * Created by nafanya on 3/25/16.
 */
public class DocumentsListFragment extends DocumentsListPresenterFragment implements
        BottomMenu.MenuEventListener,
        ErrorOpenDialog.Callback,
        OfflineAdapter.ItemEventListener,
        RenameDialog.Callback,
        DeleteDialog.Callback {

    public interface Callbacks {
        boolean isStoragePermissionGranted();
        void notifyOther();
        void notifyOtherItem(VkDocument document);
    }
    private Callbacks activity;

    public static final String CONTEXT_DOC_KEY = "context_doc_key";
    public static final String CONTEXT_POS_KEY = "context_pos_key";

    private BottomSheetDialog dialog;
    private VkDocument restoreContextMenuDoc;
    private int restoreDocPosition;
    private FileFormatter fileFormatter;

    public static DocumentsListFragment newInstance(boolean isOffline, VkDocument.ExtType type, SortMode sortMode, String searchQuery) {
        DocumentsListFragment fragment = new DocumentsListFragment();
        Bundle args = new Bundle();
        args.putBoolean(OFFLNE_KEY, isOffline);
        args.putSerializable(EXT_TYPE_KEY, type);
        args.putSerializable(SORT_MODE_KEY, sortMode);
        args.putString(SEARCH_QUERY_KEY, searchQuery);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App app = (App)getActivity().getApplication();
        fileFormatter = app.getFileFormatter();
        if (savedInstanceState != null) {
            restoreContextMenuDoc = savedInstanceState.getParcelable(CONTEXT_DOC_KEY);
            restoreDocPosition = savedInstanceState.getInt(CONTEXT_POS_KEY);
        }
        if (restoreContextMenuDoc != null) {
            onClickContextMenu(restoreDocPosition, restoreContextMenuDoc);
        }
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            this.activity = (Callbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putParcelable(CONTEXT_DOC_KEY, restoreContextMenuDoc);
        state.putInt(CONTEXT_POS_KEY, restoreDocPosition);
        super.onSaveInstanceState(state);
    }

    @Override
    protected BaseSortedAdapter newAdapter() {
        if (!isOffline) {
            return new DocumentsAdapter(getActivity(), fileFormatter, sortMode, this);
        } else {
            return new OfflineAdapter(getActivity(), fileFormatter, sortMode, this);
        }
    }

    @Override
    public void onRefresh() {
        setRefresh(true);
        presenter.forceNetworkLoad();
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
//        adapter.notifyItemChanged(position);
    }

    @Override
    public void onClick(int position, VkDocument document) {
        Intent intent = new Intent(getActivity(), DocumentViewerActivity.class);
        intent.putParcelableArrayListExtra(DocumentViewerActivity.DOCUMENTS_KEY, (ArrayList<VkDocument>)adapter.getData());
        intent.putExtra(DocumentViewerActivity.POSITION_KEY, position);
        startActivity(intent);
    }

    @Override
    public void onClickContextMenu(int position, VkDocument document) {
        restoreContextMenuDoc = document;
        this.restoreDocPosition = position;
        dialog = new BottomMenu(getActivity(), position, document, fileFormatter, this);
        dialog.show();
    }

    /***BottomMenu callbacks***/
    @Override
    public void onClickMakeOffline(int position, VkDocument document, boolean isMakeOffline) {
        Timber.d("[Bottom menu] clicked offline button for doc (title=%s, isMakeOffline=%s)", document.title, isMakeOffline);
        if (isMakeOffline) {
            presenter.makeOffline(document);
        } else {
            presenter.undoMakeOffline(document);
        }
    }

    @Override
    public void onClickRename(int position, VkDocument document) {
        dismissContextMenu();
        DialogFragment fragment = RenameDialog.newInstance(position, document);
        fragment.setTargetFragment(this, 0);
        fragment.show(getActivity().getSupportFragmentManager(), "rename");
    }

    @Override
    public void onClickDelete(int position, VkDocument document) {
        dismissContextMenu();
        DialogFragment fragment = DeleteDialog.newInstance(position, document);
        fragment.setTargetFragment(this, 0);
        fragment.show(getActivity().getSupportFragmentManager(), "delete");
    }

    @Override
    public void onCloseContextMenu() {
        dismissContextMenu();
    }

    @Override
    public void onClickDownload(int position, VkDocument document) {
        if (activity.isStoragePermissionGranted()) {
            presenter.downloadDocumentToDownloads(document);
        }
    }

    // TODO: [fragment] fix share
    @Override
    public void onClickShare(VkDocument document) {
        dismissContextMenu();
        Intent intent = createShareIntent(document);

        // Try to find VK app and set it if found.
        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> resInfo = pm.queryIntentActivities(intent, 0);
        boolean vkAppFound = false;
        for (ResolveInfo info : resInfo) {
            String packageName = info.activityInfo.packageName;
            if (packageName.contains("com.vkontakte.android")) {
                Timber.d("[share] uri: found VK app, using it");
                intent.setPackage(packageName);
                vkAppFound = true;
                break;
            }
        }
        if (vkAppFound) {
            startActivity(intent);
        } else {
            startActivity(Intent.createChooser(intent, document.title));
        }
    }

    @Override
    public void onClickShareExternal(VkDocument document) {
        dismissContextMenu();
        Intent intent = createShareIntent(document);
        startActivity(Intent.createChooser(intent, document.title));
    }

    private Intent createShareIntent(VkDocument document) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        final Uri uri;
        final String mime;
        if (document.isCached() || document.isOffline()) {
            uri = Uri.fromFile(new File(document.getPath()));
        } else {
            uri = Uri.parse(document.url);
        }
        MimeTypeMap mimeResolver = MimeTypeMap.getSingleton();
        mime = mimeResolver.getMimeTypeFromExtension(document.getExt());
        Timber.d("[share] uri: %s", uri);

        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType(mime);
        Timber.d("[share] mimetype: %s", mime);
        return intent;
    }

    public void dismissContextMenu() {
        dialog.dismiss();
        dialog = null;
        restoreContextMenuDoc = null;
        restoreDocPosition = -1;
    }

    @Override
    public void onDatabaseError(Exception ex) {

    }

    @Override
    public void onUpdatedDocument(VkDocument document) {
        adapter.notifyItemChanged(adapter.getData().indexOf(document));
        activity.notifyOther();
    }

    /***ErrorOpen dialog callbacks***/
    @Override
    public void onRetry(VkDocument document, boolean isAlreadyDownloading) {
        presenter.retryDownloadDocument(document);
    }

    @Override
    public void onCancel(VkDocument document, boolean isAlreadyDownloading) {
        if (!isAlreadyDownloading) {
            presenter.cancelDownloading(document);
        }
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
        activity.notifyOtherItem(document);
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

    public void changeDocumentType(VkDocument.ExtType type) {
        Timber.d("[fragment] Setting new document type: %s", type);
        documentType = type;
        presenter.setFilter(getFilter());
        presenter.getDocuments();
        setupEmptyView();
    }

    public void changeSearchQuery(String query) {
        Timber.d("[fragment] Setting new search query: %s", query);
        searchQuery = query;
        adapter.setSearchFilter(query);
    }

    public void changeSortMode(SortMode sortMode) {
        Timber.d("[fragment] Setting new sort mode: %s", sortMode);
        this.sortMode = sortMode;
        adapter.setSortMode(sortMode);
    }

    public void updateDocumentList() {
        presenter.setFilter(getFilter());
        presenter.getDocuments();
        Timber.d("[fragment] calling getDocuments for %s", isOffline);
    }

    public void updateDocumentListItem(VkDocument document) {
        adapter.notifyItemChanged(adapter.getData().indexOf(document));
    }
}
