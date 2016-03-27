package io.github.nafanya.vkdocs.presentation.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.utils.FileFormatter;

public class RenameDialog extends DialogFragment {
    private static String DOC_KEY = "doc_key";
    private static String POS_KEY = "pos_key";

    private FileFormatter fileFormatter;

    @Bind(R.id.ic_document_type)
    ImageView documentTypeIcon;

    @Bind(R.id.text_document_title)
    EditText docTitle;

    public interface Callback {
        void onCancelRename(VkDocument document);
        void onRename(int position, VkDocument document, String newName);
    }
    private Callback callback;
    private int position;
    private VkDocument doc;

    public static RenameDialog newInstance(int position, VkDocument document) {
        RenameDialog fragment = new RenameDialog();
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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.rename_dialog, null);
        builder.setView(rootView).setNegativeButton(R.string.default_cancel_button, (dialog, which) -> {
            callback.onCancelRename(doc);
            dismiss();
        }).setPositiveButton(R.string.default_confirm_button, (dialog, which) -> {
            callback.onRename(position, doc, docTitle.getText().toString());
            dismiss();
        });
        Dialog dialog = builder.create();
        ButterKnife.bind(this, rootView);
        dialog.setCanceledOnTouchOutside(false);

        documentTypeIcon.setImageDrawable(fileFormatter.getIcon(doc, getActivity()));
        docTitle.setText(doc.title);
        return dialog;
    }
}
