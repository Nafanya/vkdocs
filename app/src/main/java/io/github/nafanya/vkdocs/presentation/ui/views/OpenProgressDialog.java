package io.github.nafanya.vkdocs.presentation.ui.views;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
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

    private static String DOC_ID_KEY = "doc_id_key";
    private static String DOC_TITLE_KEY = "doc_title_key";

    private DownloadRequest request;
    private String title;
    private FileFormatter fileFormatter;

    @Bind(R.id.text_document_title)
    TextView docTitle;

    @Bind(R.id.down_progress)
    ProgressBar downloadProgress;

    @Bind(R.id.size)
    TextView size;

    private Callback callback;

    public static OpenProgressDialog newInstance(VkDocument document) {
        OpenProgressDialog fragment = new OpenProgressDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(DOC_ID_KEY, document.getId());
        bundle.putString(DOC_TITLE_KEY, document.title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App app = (App)getActivity().getApplication();
        fileFormatter = app.getFileFormatter();
        int docId = getArguments().getInt(DOC_ID_KEY);
        title = getArguments().getString(DOC_TITLE_KEY);

        callback = (Callback)getTargetFragment();//GET CALLBACK FRAGMENT OR ACTIVITY HERE

        InterruptableDownloadManager downloadManager = app.getDownloadManager();
        List<DownloadRequest> requests = downloadManager.getQueue();
        Timber.d("size down queue = " + requests.size());
        for (DownloadRequest r: requests) {
            if (r.getDocId() == docId) {
                request = r;
                break;
            }
            Timber.d("doc id = " + r.getDocId());
        }

        if (request == null) {
            Timber.d("request is null, download is completed faster than open dialog");
            dismiss();
            callback.onCompleteCaching();
        }
        Timber.d("ON CREATE request " + request);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.open_progress_dialog, null);
        builder.setView(rootView);
        Dialog dialog = builder.create();
        ButterKnife.bind(this, rootView);

        docTitle.setText(title);
        downloadProgress.setProgress(fileFormatter.getProgress(request));
        size.setText(fileFormatter.formatFrom(request));

        request.setObserver(this);
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
        callback.onCompleteCaching();
    }

    @Override
    public void onError(Exception e) {
        dismiss();
        callback.onErrorCaching(e);
    }

    //Indeterminate progress cannot be infinite
    @Override
    public void onInfiniteProgress() {
        downloadProgress.setIndeterminate(true);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Timber.d("ON CANCEL DIALOG");
        callback.onCancelCaching();
    }

    public interface Callback {
        void onCancelCaching();
        void onCompleteCaching();
        void onErrorCaching(Exception error);
    }
}
