package io.github.nafanya.vkdocs.presentation.ui.views.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

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


public class UnknownTypeDocFragment extends Fragment
        implements DocumentViewerPresenter.Callback, OnPageChanged {

    private static String DOC_KEY = "doc_key";

    private VkDocument document;
    private FileFormatter fileFormatter;

    @Bind(R.id.unknown_type_layout)
    RelativeLayout relativeLayout;

    @Bind(R.id.file_type_icon)
    ImageView typeIcon;

    @Bind(R.id.file_name)
    TextView fileName;

    @Bind(R.id.progressBar)
    CircularProgressBar downloadProgress;

    @Bind(R.id.downloaded_size)
    TextView downloadedSize;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_unknown_type_document, container, false);

        ButterKnife.bind(this, rootView);
        typeIcon.setImageDrawable(fileFormatter.getPlaceholder(document, getActivity()));
        fileName.setText(document.title);
        typeIcon.setOnClickListener(v -> {
            if (document.isDownloaded())
                throwIntentToOpen();

        });

        if (!document.isDownloaded())
            downloadedSize.setText(fileFormatter.formatFrom(0, document.size));
        else
            whenDownloaded();
        return rootView;
    }

    protected void whenDownloaded() {
        downloadedSize.setText(fileFormatter.formatSize(document.size));
        downloadProgress.setVisibility(View.GONE);

        /*Timber.d("rel layout = " + relativeLayout);
        Snackbar snackbar = Snackbar
                .make(relativeLayout, "Open in other app", Snackbar.LENGTH_INDEFINITE)
                .setAction("OPEN", view -> {
                    Timber.d("on click open");
                });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();*/
    }

    protected void throwIntentToOpen() {
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String mimeType = myMime.getMimeTypeFromExtension(document.getExt());
        Timber.d("[openDocument] mime: %s, title: %s", mimeType, document.title);
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

    /***Presenter callbacks**/
    @Override
    public void onProgress(int percentage) {
        long downSize = document.size * percentage / 100;
        downloadedSize.setText(fileFormatter.formatFrom(downSize, document.size));
        downloadProgress.setProgress(percentage);
    }

    @Override
    public void onCompleteCaching(VkDocument document) {
        whenDownloaded();
    }

    @Override
    public void onError(Exception e) {

    }
    /***End callbacks***/

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
    public void onResume() {
        super.onResume();
        if (isBecameVisible)
            onBecameVisible();
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

    @Override
    public void onBecameVisible() {
        if (isAlreadyNotifiedAboutVisible)
            return;
        isAlreadyNotifiedAboutVisible = true;
        presenter.openDocument(document);
    }

    @Override
    public void onBecameInvisible() {
        isAlreadyNotifiedAboutVisible = false;
        if (presenter.isDownloading()) {
            presenter.cancelDownloading();
            downloadProgress.setProgress(0);
        }
    }
}
