package io.github.nafanya.vkdocs.presentation.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import io.github.nafanya.vkdocs.R;

/**
 * Created by nafanya on 3/27/16.
 */
public class LogoutDialog extends AppCompatDialogFragment {

    public interface Callback {
        void onLogoutConfirmed();
    }

    private Callback callback;

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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setMessage(R.string.logout_message)
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    dismiss();
                }).setPositiveButton(R.string.logout, (dialog, which) -> {
                    callback.onLogoutConfirmed();
                    dismiss();
                });
        return builder.create();
    }

}