package io.github.nafanya.vkdocs.presentation.ui.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.services.AudioPlayerService;

public class MusicPlayFragment extends Fragment {
    public static String MUSIC_KEY = "music_key";

    private VkDocument audioDocument;
    private Player callback;

    public interface Player {
        AudioPlayerService player();
    }

    public static MusicPlayFragment newInstance(VkDocument document) {
        MusicPlayFragment fragment = new MusicPlayFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MUSIC_KEY, document);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            callback = (Player) activity;//get player for pause, resume
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Callback");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioDocument = getArguments().getParcelable(MUSIC_KEY);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!callback.player().isNowPlaying(audioDocument)) {
            try {
                callback.player().play(audioDocument);
            } catch (IOException ignore) {
                //TODO wtf7
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
