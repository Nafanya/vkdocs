package io.github.nafanya.vkdocs.presentation.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.presentation.ui.SortMode;

/**
 * Created by nafanya on 3/12/16.
 */
public class SortByDialog extends AppCompatDialogFragment {

    private static final String POSITION = "position";

    private Callback listener;

    public static SortByDialog create(SortMode mode) {
        Bundle args = new Bundle();
        args.putSerializable(POSITION, mode);
        SortByDialog dialogFragment = new SortByDialog();
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (Callback)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Callback");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        SortMode mode = null;
        if (args != null) {
            mode = (SortMode) args.getSerializable(POSITION);
        }
        final int position = SortMode.toInt(mode);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.action_sort_by)
                .setSingleChoiceItems(R.array.sort_by_labels, position, (dialog, which) -> {
                    if (listener != null) {
                        SortMode newSortMode = SortMode.toMode(which);
                        listener.onSortModeChanged(newSortMode);
                        dismiss();
                    }
                });
        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        setTargetFragment(null, 0);
    }

    public interface Callback {
        void onSortModeChanged(SortMode sortMode);
    }

}
