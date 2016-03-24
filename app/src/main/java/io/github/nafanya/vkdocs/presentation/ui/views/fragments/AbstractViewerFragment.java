package io.github.nafanya.vkdocs.presentation.ui.views.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import io.github.nafanya.vkdocs.presentation.ui.views.activities.DocumentViewerActivity;
import timber.log.Timber;

public abstract class AbstractViewerFragment extends Fragment implements DocumentViewerActivity.OnPageChanged  {
    public static String FIRST_KEY = "first_key";
    protected boolean isThisFirstFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isThisFirstFragment = getArguments().getBoolean(FIRST_KEY);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isThisFirstFragment) {
            isThisFirstFragment = false;
            onBecameVisible();
        }
    }

    @Override
    public final void onSetFirst(boolean isFirst) {
        isThisFirstFragment = isFirst;
    }
}
