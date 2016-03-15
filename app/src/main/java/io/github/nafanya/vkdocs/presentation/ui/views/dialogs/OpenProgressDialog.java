package io.github.nafanya.vkdocs.presentation.ui.views.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.download.InterruptableDownloadManager;
import io.github.nafanya.vkdocs.domain.download.base.DownloadManager;
import io.github.nafanya.vkdocs.domain.download.base.DownloadRequest;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.utils.FileFormatter;
import timber.log.Timber;


public class OpenProgressDialog extends AppCompatDialogFragment implements DownloadManager.RequestObserver {

    private static String DOC_KEY = "doc_key";
    private static String ALREADY_DOWNLOADING_KEY = "adlready_downloading";

    private DownloadRequest request;
    private VkDocument doc;
    private boolean isAlreadyDownloading;
    private FileFormatter fileFormatter;

    @Bind(R.id.ic_document_type)
    ImageView documentTypeIcon;

    @Bind(R.id.text_document_title)
    TextView docTitle;

    @Bind(R.id.down_progress)
    ProgressBar downloadProgress;

    @Bind(R.id.size)
    TextView size;

    private Callback callback;

    public static OpenProgressDialog newInstance(VkDocument document, boolean isAlreadyDownloading) {
        OpenProgressDialog fragment = new OpenProgressDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(DOC_KEY, document);
        bundle.putBoolean(ALREADY_DOWNLOADING_KEY, isAlreadyDownloading);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        try {
            callback = (Callback)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Callback");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doc = getArguments().getParcelable(DOC_KEY);
        isAlreadyDownloading = getArguments().getBoolean(ALREADY_DOWNLOADING_KEY);

        App app = (App)getActivity().getApplication();
        fileFormatter = app.getFileFormatter();

        InterruptableDownloadManager downloadManager = app.getDownloadManager();
        List<DownloadRequest> requests = downloadManager.getQueue();

        for (DownloadRequest r: requests)
            if (r.getDocId() == doc.getId()) {
                request = r;
                break;
            }

        if (request == null) {
            Timber.d("request is null, title: %s", doc.title);
            dismiss();
            callback.onCompleteCaching(doc);
        }
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

        Timber.d("doc type ic = " + documentTypeIcon + ", fileformatter = " + fileFormatter + ", doc = " + doc);
        documentTypeIcon.setImageDrawable(fileFormatter.getIcon(doc, getActivity()));
        docTitle.setText(doc.title);
        if (request != null) {
            downloadProgress.setProgress(fileFormatter.getProgress(request));
            size.setText(fileFormatter.formatFrom(request));
            request.setObserver(this);
        }
        return dialog;
    }

    @Override
    public void onProgress(int percentage) {
        size.setText(fileFormatter.formatFrom(request));
        downloadProgress.setProgress(percentage);
    }

    @Override
    public void onComplete() {
        Timber.d("on complete");
        dismiss();
        doc.setPath(request.getDest());
        callback.onCompleteCaching(doc);
    }

    @Override
    public void onError(Exception e) {
        dismiss();
        callback.onErrorCaching(e, isAlreadyDownloading);
    }

    //Indeterminate progress cannot be infinite
    @Override
    public void onInfiniteProgress() {
        downloadProgress.setIndeterminate(true);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Timber.d("ON CANCEL DIALOG");
        // TODO: callback is null after back button pressed -> NPE
        callback.onCancelCaching(doc, isAlreadyDownloading);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        setTargetFragment(null, 0);
    }


    public interface Callback {
        void onCancelCaching(VkDocument document, boolean isAlreadyDownloading);
        void onCompleteCaching(VkDocument document);
        void onErrorCaching(Exception error, boolean isAlreadyDownloading);
    }
}
