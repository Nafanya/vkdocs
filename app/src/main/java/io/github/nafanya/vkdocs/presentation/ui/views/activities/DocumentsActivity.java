package io.github.nafanya.vkdocs.presentation.ui.views.activities;

import android.support.design.widget.BottomSheetDialog;

import java.util.Collections;
import java.util.List;

import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.SortMode;
import io.github.nafanya.vkdocs.presentation.ui.adapters.DocumentsAdapter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.OfflineAdapter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.base.BaseSortedAdapter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.base.CommonItemEventListener;
import io.github.nafanya.vkdocs.presentation.ui.views.dialogs.BottomMenu;
import io.github.nafanya.vkdocs.presentation.ui.views.dialogs.OpenProgressDialog;
import timber.log.Timber;

/**
 * Created by pva701 on 15.03.16.
 */
public class DocumentsActivity extends PresenterActivity implements
        BottomMenu.MenuEventListener,
        OpenProgressDialog.Callback,
        OfflineAdapter.ItemEventListener {

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
        adapter = null;
        presenter.getDocuments();
    }

    /***Adapter callback***/
    @Override
    public void onClick(int position, VkDocument document) {
        if (document.getExtType() != VkDocument.ExtType.AUDIO &&
                document.getExtType() != VkDocument.ExtType.VIDEO &&
                document.getExtType() != VkDocument.ExtType.IMAGE) {
            Timber.d("on click: %s, offtype = %d, request %s", document.title, document.getOfflineType(), document.getRequest());
            Timber.d("is off %b, is cached %b", document.isOffline(), document.isCached());
            presenter.openDocument(document);
        } else {
            //TODO
        }
    }

    @Override
    public void onClickContextMenu(int position, VkDocument document) {
        App app = (App)getApplication();
        BottomSheetDialog dialog = new BottomMenu(this, document, app.getFileFormatter(), this);
        dialog.show();
    }


    @Override
    public void onCancelDownloading(int position, VkDocument document) {
        presenter.cancelDownloading(document);
        ((OfflineAdapter) adapter).removeIndex(position);
    }


    @Override
    public void onCompleteDownloading(int position, VkDocument document) {
        presenter.updateDocument(document);
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
        if (!isAlreadyDownloading)
            presenter.cancelDownloading(document);
    }

    @Override
    public void onCompleteCaching(VkDocument document) {
        openDocument(document);
    }

    @Override
    public void onErrorCaching(Exception error, boolean isAlreadyDownloading) {

    }

    @Override
    protected BaseSortedAdapter newAdapter() {
        if (navDrawerPos == 1)
            return new DocumentsAdapter(this, fileFormatter, sortMode, this);
        else
            return new OfflineAdapter(this, fileFormatter, sortMode, this);
    }
}
