package io.github.nafanya.vkdocs.presentation.ui.views.base;

import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import com.vk.sdk.api.model.VKApiDocument;

import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.MyDocsAdapter;
import timber.log.Timber;

public abstract class BlockedOpeningListFragment<T extends DocumentsPresenter, A extends MyDocsAdapter> extends AbstractMyDocsListFragment<T, A> {
    @Override
    public void onClick(int position, VKApiDocument document) {
        Timber.d("ON CLICK ITEM pos = " + position + ", this = " + this);
        //TODO implement here

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.open_progress_dialog, null));
        builder.create().show();
    }
}
