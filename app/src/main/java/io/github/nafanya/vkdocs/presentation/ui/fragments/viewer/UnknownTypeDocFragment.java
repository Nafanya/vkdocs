package io.github.nafanya.vkdocs.presentation.ui.fragments.viewer;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import io.github.nafanya.vkdocs.presentation.ui.fragments.viewer.base.DownloadableDocFragment;
import io.github.nafanya.vkdocs.utils.FileFormatter;
import timber.log.Timber;


public class UnknownTypeDocFragment extends DownloadableDocFragment {
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


    public static UnknownTypeDocFragment newInstance(VkDocument document) {
        UnknownTypeDocFragment fragment = new UnknownTypeDocFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(DOC_KEY, document);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App app = (App)getActivity().getApplication();
        fileFormatter = app.getFileFormatter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_unknown_type_document, container, false);

        ButterKnife.bind(this, rootView);
        typeIcon.setImageDrawable(getPlaceholder(document));
        fileName.setText(document.title);
        typeIcon.setOnClickListener(v -> {
            if (presenter.isDownloaded())
                throwIntentToOpen(getActivity(), document);
        });
        if (!presenter.isDownloaded())
            downloadedSize.setText(fileFormatter.formatFrom(0, document.size));
        else {
            hideProgress();
            //downloadedSize.setText(fileFormatter.formatSize(document.size));
        }
        return rootView;
    }

    protected void showProgress() {
        downloadedSize.setText(fileFormatter.formatFrom(0, document.size));
        downloadProgress.setVisibility(View.VISIBLE);
        hideSnackbar();
    }

    protected void hideProgress() {
        downloadedSize.setText(fileFormatter.formatSize(document.size));
        downloadProgress.setVisibility(View.GONE);

        if (!presenter.isDownloaded()) {
            downloadProgress.setProgress(0);
            downloadedSize.setText(fileFormatter.formatFrom(0, document.size));
        }
    }

    @Override
    public View rootForSnackbar() {
        return relativeLayout;
    }

    protected void whenDownloaded() {
        Timber.d("wnen downloaded = " + document.title);
        hideProgress();

        showSnackBar(v -> throwIntentToOpen(getActivity(), document));
    }

    public static void throwIntentToOpen(Context context, VkDocument document) {
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String mimeType = myMime.getMimeTypeFromExtension(document.getExt());
        Timber.d("[openDocument] mime: %s, title: %s", mimeType, document.title);
        File fileDoc = new File(document.getPath());
        newIntent.setDataAndType(Uri.fromFile(fileDoc), mimeType);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//TODO one task?
        try {
            context.startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            //TODO do something
            //Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }
    }

    /***Presenter callbacks**/
    @Override
    public void onProgress(int percentage) {
        hideErrorSnackbar();
        long downSize = document.size * percentage / 100;
        downloadedSize.setText(fileFormatter.formatFrom(downSize, document.size));
        downloadProgress.setProgress(percentage);
    }

    @Override
    public void onCompleteCaching(VkDocument document) {
        this.document = document;
        whenDownloaded();
    }

    /***Fragment callbacsk***/

    @Override
    public void onBecameVisible() {
        super.onBecameVisible();
        if (!presenter.isDownloaded()) {
            showProgress();
            presenter.openDocument();
        } else
            whenDownloaded();
    }

    @Override
    public void onBecameInvisible() {
        super.onBecameInvisible();
        hideProgress();
    }
}
