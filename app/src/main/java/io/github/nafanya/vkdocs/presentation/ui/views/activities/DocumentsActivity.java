package io.github.nafanya.vkdocs.presentation.ui.views.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.webkit.MimeTypeMap;

import java.io.File;

import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.ui.SortMode;
import io.github.nafanya.vkdocs.presentation.ui.views.dialogs.BottomMenu;
import io.github.nafanya.vkdocs.presentation.ui.views.dialogs.OpenProgressDialog;
import io.github.nafanya.vkdocs.presentation.ui.views.fragments.BaseDocumentsFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.fragments.DocumentsFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.fragments.OfflineDocumentsFragment;
import timber.log.Timber;

/**
 * Created by pva701 on 15.03.16.
 */
public class DocumentsActivity extends BaseActivity implements
        BaseDocumentsFragment.Callback,
        BottomMenu.MenuEventListener,
        OpenProgressDialog.Callback {

    //private DocumentsPresenter presenter;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        onSectionChanged(1, extType, sortMode);
    }

    /***BaseActivity overrides***/
    @Override
    public void onExtOrSortChanged(VkDocument.ExtType extType, SortMode sortMode) {

    }

    private DocumentsPresenter presenter;

    @Override
    public void onSectionChanged(int newPos, VkDocument.ExtType extType, SortMode sortMode) {
        Timber.d("ON SECTION CHANGED!");
        BaseDocumentsFragment fragment;
        if (newPos == 1)
            fragment = DocumentsFragment.newInstance(extType, sortMode);
        else
            fragment = OfflineDocumentsFragment.newInstance(extType, sortMode);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        presenter = fragment.presenter();
        Timber.d("presenter = " + presenter);
    }

    /***BaseDocumentsFragments callbacks***/
    @Override
    public void onClickContextMenu(int position, VkDocument document) {
        App app = (App)getApplication();
        BottomSheetDialog dialog = new BottomMenu(this, document, app.getFileFormatter(), this);
        dialog.show();
    }

    @Override
    public void onClick(int position, VkDocument document) {
        if (document.getExtType() != VkDocument.ExtType.AUDIO &&
                document.getExtType() != VkDocument.ExtType.VIDEO &&
                document.getExtType() != VkDocument.ExtType.IMAGE) {
           presenter.openDocument(document);
        } else {
            //TODO
        }
    }

    public void openDocument(VkDocument document) {
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

    /***BottomMenu callbacks***/
    @Override
    public void onClickMakeOffline(VkDocument document, boolean isMakeOffline) {
        Timber.d("[Bottom menu] clicked offline button for doc (title=%s, isMakeOffline=%s)", document.title, isMakeOffline);
        if (isMakeOffline) {
            presenter.makeOffline(document);
        }
    }

    /***OpenProgressDialog callbacks***/
    @Override
    public void onCancelCaching(VkDocument document, boolean isAlreadyDownloading) {
        if (!isAlreadyDownloading) {
            presenter.cancelDownloading(document);
        }
    }

    @Override
    public void onCompleteCaching(VkDocument document) {
        presenter.updateDocument(document);
        openDocument(document);
    }

    @Override
    public void onErrorCaching(Exception error, boolean isAlreadyDownloading) {

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
}
