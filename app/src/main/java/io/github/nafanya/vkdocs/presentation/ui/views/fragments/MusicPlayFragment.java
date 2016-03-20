package io.github.nafanya.vkdocs.presentation.ui.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.AudioPlayer;

public class MusicPlayFragment extends Fragment {
    public static String MUSIC_KEY = "music_key";

    private VkDocument audioDocument;
    private AudioPlayer player;
    public static MusicPlayFragment newInstance(VkDocument document) {
        MusicPlayFragment fragment = new MusicPlayFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MUSIC_KEY, document);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioDocument = getArguments().getParcelable(MUSIC_KEY);
        if (audioDocument.getPath() != null)
            player = new AudioPlayer(getActivity().getApplication(), audioDocument.getPath());
        else
            player = new AudioPlayer(getActivity().getApplication(), audioDocument.url);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        player.play();
    }

    @Override
    public void onStop() {
        player.stop();
        super.onStop();
    }
}
