package io.github.nafanya.vkdocs.presentation.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.utils.FileFormatter;

public class DeleteDialog extends AppCompatDialogFragment {

    private static String DOC_KEY = "doc_key";
    private static String POS_KEY = "pos_key";
    private FileFormatter fileFormatter;

    public interface Callback {
        void onCancelDelete(VkDocument document);
        void onDelete(int position, VkDocument document);
    }

    private Callback callback;
    private int position;
    private VkDocument doc;

    public static DeleteDialog newInstance(int position, VkDocument document) {
        DeleteDialog fragment = new DeleteDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(DOC_KEY, document);
        bundle.putInt(POS_KEY, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doc = getArguments().getParcelable(DOC_KEY);
        position = getArguments().getInt(POS_KEY);
        callback = (Callback) getTargetFragment();

        App app = (App)getActivity().getApplication();
        fileFormatter = app.getFileFormatter();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String title = getResources().getString(R.string.delete_document_title);
        String delete_label = getResources().getString(R.string.delete) + " ";

        builder.setTitle(title).setMessage(delete_label + doc.title + "?").
                setNegativeButton(R.string.default_cancel_button, (dialog, which) -> {
                    callback.onCancelDelete(doc);
                    dismiss();
            }).setPositiveButton(R.string.delete, (dialog, which) -> {
                callback.onDelete(position, doc);
                dismiss();
            });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
