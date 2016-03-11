package io.github.nafanya.vkdocs.presentation.ui.views.base;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.webkit.MimeTypeMap;

import java.io.File;

import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.DocFilter;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.ExtDocFilter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.base.CommonItemEventListener;

public abstract class AbstractListFragment<AdapterType>
        extends Fragment implements DocumentsPresenter.Callback, CommonItemEventListener {
    public static String EXT_TYPE_KEY = "ext_types_key";

    public static DocFilter ALL = new ExtDocFilter(
                    VkDocument.ExtType.TEXT,
                    VkDocument.ExtType.BOOK,
                    VkDocument.ExtType.ARCHIVE,
                    VkDocument.ExtType.GIF,
                    VkDocument.ExtType.IMAGE,
                    VkDocument.ExtType.AUDIO,
                    VkDocument.ExtType.VIDEO,
                    VkDocument.ExtType.UNKNOWN);



    protected DocumentsPresenter presenter;
    protected AdapterType adapter;

    protected abstract AdapterType newAdapter();

    protected DocumentsPresenter newPresenter() {
        App app = (App)getActivity().getApplication();

        DocFilter filter = (DocFilter) getArguments().get(EXT_TYPE_KEY);
        return new DocumentsPresenter(
                filter,
                app.getEventBus(),
                app.getRepository(),
                app.getDownloadManager(),
                app.getInternetService(),
                this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = newPresenter();
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
    public void onClick(int position, VkDocument document) {
        //new OpenProgressDialog().show();

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
        String mimeType = myMime.getMimeTypeFromExtension("." + document.getExt());

        File fileDoc = new File("");//TODO write here path
        newIntent.setDataAndType(Uri.fromFile(fileDoc), mimeType);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//TODO one task?
        try {
            getActivity().startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            //TODO do something
            //Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onOpenFile(VkDocument document) {
        //TODO new intent for view
    }

    @Override
    public void onDownloadingFile(VkDocument document) {

    }

    @Override
    public void onNoInternetWhenOpen() {
        //TODO snackbar or smth shit
    }
}
