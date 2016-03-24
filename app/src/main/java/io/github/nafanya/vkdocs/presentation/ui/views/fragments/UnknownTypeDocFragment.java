package io.github.nafanya.vkdocs.presentation.ui.views.fragments;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.presenter.DocumentViewerPresenter;
import io.github.nafanya.vkdocs.presentation.ui.views.fragments.base.OnPageChanged;
import io.github.nafanya.vkdocs.utils.FileFormatter;
import timber.log.Timber;


public class UnknownTypeDocFragment extends AppCompatDialogFragment
        implements DocumentViewerPresenter.Callback,
        OnPageChanged {

    private static String DOC_KEY = "doc_key";

    private VkDocument document;
    private FileFormatter fileFormatter;

    @Bind(R.id.ic_document_type)
    ImageView documentTypeIcon;

    @Bind(R.id.text_document_title)
    TextView docTitle;

    @Bind(R.id.down_progress)
    ProgressBar downloadProgress;

    @Bind(R.id.statusLabels)
    TextView size;


    private DocumentViewerPresenter presenter;

    public static UnknownTypeDocFragment newInstance(VkDocument document) {
        UnknownTypeDocFragment fragment = new UnknownTypeDocFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(DOC_KEY, document);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        document = getArguments().getParcelable(DOC_KEY);

        App app = (App)getActivity().getApplication();
        presenter = new DocumentViewerPresenter(
                app.getEventBus(),
                app.getRepository(),
                app.getCacheManager(), this);

        fileFormatter = app.getFileFormatter();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.open_progress_dialog, null);
        builder.setView(rootView);
        Dialog dialog = builder.create();
        ButterKnife.bind(this, rootView);
        dialog.setCanceledOnTouchOutside(false);

        documentTypeIcon.setImageDrawable(fileFormatter.getIcon(document, getActivity()));
        docTitle.setText(document.title);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void onStop() {
        presenter.onStop();
        super.onStop();
    }

    /***Presenter callbacks**/
    @Override
    public void onProgress(int percentage) {
        size.setText(fileFormatter.formatFrom(document.getRequest()));
        downloadProgress.setProgress(percentage);
    }

    @Override
    public void onCompleteCaching(VkDocument document) {
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

    @Override
    public void onError(Exception e) {

    }

    private boolean isBecameVisible = false;
    private boolean isAlreadyNotifiedAboutVisible = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser != isBecameVisible) {
            isBecameVisible = isVisibleToUser;
            if (isBecameVisible) {
                if (isResumed())
                    onBecameVisible();
            } else
                onBecameInvisible();
        }
    }

    @Override
    public void onBecameVisible() {
        if (isAlreadyNotifiedAboutVisible)
            return;
        isAlreadyNotifiedAboutVisible = true;
        presenter.openDocument(document);
        //show(getFragmentManager(), "unknow_type");
    }

    @Override
    public void onBecameInvisible() {
        isAlreadyNotifiedAboutVisible = false;
        if (presenter.isDownloading()) {
            presenter.cancelDownloading();
            downloadProgress.setProgress(0);
        }
        //dismiss();
    }
}
