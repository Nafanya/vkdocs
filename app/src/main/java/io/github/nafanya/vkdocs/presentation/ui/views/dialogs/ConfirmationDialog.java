package io.github.nafanya.vkdocs.presentation.ui.views.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.utils.FileFormatter;

public class ConfirmationDialog extends AppCompatDialogFragment {
    public interface Callback {
        void onConfirm(int label);
    }

    public static final String LABEL_KEY = "label_key";
    public static final String TITLE_KEY = "title_key";
    public static final String MESSAGE_KEY = "message_key";
    public static final String CONFIRM_BUTTON_KEY = "confirm_button_key";
    public static final String CANCEL_BUTTON_KEY = "cancel_button_key";

    private int label;
    private String title;
    private String message;
    private String confirmButton;
    private String cancelButton;
    private Callback callback;

    public static ConfirmationDialog newInstance(int label, String title, String message) {
        ConfirmationDialog fragment = new ConfirmationDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(LABEL_KEY, label);
        bundle.putString(TITLE_KEY, title);
        bundle.putString(MESSAGE_KEY, message);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        try {
            callback = (Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Callback");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        label = getArguments().getInt(LABEL_KEY);
        title = getArguments().getString(TITLE_KEY);
        message = getArguments().getString(MESSAGE_KEY);

        confirmButton = getResources().getString(R.string.default_confirm_button);
        cancelButton = getResources().getString(R.string.default_cancel_button);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (title != null)
            builder.setTitle(title);
        //builder.setTitl
        builder.setMessage(message).
                setNegativeButton(cancelButton, (dialog, which) -> {
                    dismiss();
                }).setPositiveButton(confirmButton, (dialog, which) -> {
                    callback.onConfirm(label);
                    dismiss();
                });
        return builder.create();
    }

    /*public static ConfirmationDialog newInstance(int label, String title, String message, String buttonConfirm, String buttonCancel) {
        ConfirmationDialog fragment = ConfirmationDialog.newInstance(label, title, )
    }*/
}
