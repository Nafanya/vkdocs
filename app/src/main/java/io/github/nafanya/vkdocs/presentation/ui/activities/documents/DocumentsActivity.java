package io.github.nafanya.vkdocs.presentation.ui.activities.documents;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by pva701 on 15.03.16.
 */
public class DocumentsActivity extends AppCompatActivity {
    private static String CONTEXT_DOC_KEY = "context_doc_key";
    private static String CONTEXT_POS_KEY = "context_pos_key";
/*
    private BottomSheetDialog dialog;
    private VkDocument restoreContextMenuDoc;
    private int restoreDocPosition;
    private FileFormatter fileFormatter;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        App app = (App)getApplication();
        fileFormatter = app.getFileFormatter();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putParcelable(CONTEXT_DOC_KEY, restoreContextMenuDoc);
        state.putInt(CONTEXT_POS_KEY, restoreDocPosition);
        super.onSaveInstanceState(state);
    }

    //DocumentsBaseActivity overrides
    @Override
    public void onTypeFilterChanged(VkDocument.ExtType documentType) {
        presenter.setFilter(getFilter(navDrawerPos, documentType));
        presenter.getDocuments();
    }

    @Override
    public void onSortModeChanged(SortMode sortMode) {
        super.onSortModeChanged(sortMode);
//        adapter.setSortMode(sortMode);
    }

    @Override
    public void onSectionChanged(int newSection) {
//        presenter.setFilter(getFilter(newSection, documentType));
//        if (adapter != null)
//            adapter.removeData();
//        adapter = null;
//        if (recyclerView != null)
//            recyclerView.scrollToPosition(0);
//        presenter.getDocuments();
    }

    @Override
    public boolean onQueryTextChange(String query) {
        //TODO: [fragment] adapter.setSearchFilter(query);

        return super.onQueryTextChange(query);
    }

    public void dismissContextMenu() {
        dialog.dismiss();
        dialog = null;
        restoreContextMenuDoc = null;
        restoreDocPosition = -1;
    }

    //Presenter callback for open document
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
    public void onUpdatedDocument(VkDocument document) {
        adapter.notifyItemChanged(adapter.getData().indexOf(document));
    }
    */
}
