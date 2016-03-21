package io.github.nafanya.vkdocs.presentation.ui.views.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import io.github.nafanya.vkdocs.domain.model.VkDocument;

/**
 * Created by nafanya on 3/21/16.
 */
public class GifImageFragment extends Fragment {
    public static final String GIF_KEY = "gif_key";

    private VkDocument document;

    public static GifImageFragment newInstance(VkDocument document) {
        GifImageFragment fragment = new GifImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(GIF_KEY, document);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        document = getArguments().getParcelable(GIF_KEY);
        if (document.isCached() || document.isOffline()) {

        }
    }
}
