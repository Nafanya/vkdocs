package io.github.nafanya.vkdocs.presentation.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import io.github.nafanya.vkdocs.R;


public class CacheSizePicker extends AppCompatDialogFragment {

    public static final String INITIAL_SIZE_KEY = "initial_size";

    public interface OnPickCacheSize {
        void onPick(int cacheSize);
    }

    private int[] sizes = new int[]{25, 50, 100, 250, 500};
    private int initialSize;
    private OnPickCacheSize callback;

    public static CacheSizePicker newInstance(int initialSize) {
        CacheSizePicker fragment = new CacheSizePicker();
        Bundle bundle = new Bundle();
        bundle.putInt(INITIAL_SIZE_KEY, initialSize);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        try {
            callback = (OnPickCacheSize) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Callback");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialSize = getArguments().getInt(INITIAL_SIZE_KEY);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] strSizes = new String[sizes.length];
        int index = 0;
        for (int i = 0; i < strSizes.length; ++i) {
            strSizes[i] = Integer.toString(sizes[i], 10);
            if (sizes[i] == initialSize)
                index = i;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.cache_size_title)
                .setSingleChoiceItems(strSizes, index, (dialog, which) -> {
                    if (initialSize != sizes[which])
                        callback.onPick(sizes[which]);
                    dismiss();
                });
        return builder.create();
    }
}
