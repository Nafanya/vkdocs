package io.github.nafanya.vkdocs.presentation.ui.dialogs;

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
public class SortByDialogFragment extends AppCompatDialogFragment {

    private static final String POSITION = "position";

    private Callback listener;

    public static SortByDialogFragment create(SortMode mode) {
        Bundle args = new Bundle();
        args.putSerializable(POSITION, mode);
        SortByDialogFragment dialogFragment = new SortByDialogFragment();
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listener = (Callback) getTargetFragment();
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

    public interface Callback {
        void onSortModeChanged(SortMode sortMode);
    }

}
