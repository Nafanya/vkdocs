package io.github.nafanya.vkdocs.presentation.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import io.github.nafanya.vkdocs.domain.model.VkDocument;

public class ErrorOpenDialog extends AppCompatDialogFragment {
    private static String DOC_KEY = "doc_key";
    private static String ALREADY_DOWNLOADING_KEY = "already_downloading";

    public interface Callback {
        void onRetry(VkDocument document, boolean isAlreadyDownloading);
        void onCancel(VkDocument document, boolean isAlreadyDownloading);
    }

    private VkDocument doc;
    private boolean isAlreadyDownloading;
    private Callback callback;

    public static ErrorOpenDialog newInstance(VkDocument document, boolean isAlreadyDownloading) {
        ErrorOpenDialog fragment = new ErrorOpenDialog();
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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        doc = getArguments().getParcelable(DOC_KEY);
        isAlreadyDownloading = getArguments().getBoolean(ALREADY_DOWNLOADING_KEY);

        //TODO move to resources, translate, Nikitos
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Error").
                setMessage("Не удалость открыть документ из-за неполадок сети.\nПроверьте интернет соединение и повторите попытку").
                setPositiveButton("Retry", (dialog, which) -> {
                    callback.onRetry(doc, isAlreadyDownloading);
                    dismiss();
                }).setNegativeButton("Cancel", (dialog, which) -> {
                    callback.onCancel(doc, isAlreadyDownloading);
                    dismiss();
                });
        return builder.create();
    }
}
