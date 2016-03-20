package io.github.nafanya.vkdocs.presentation.ui.views.fragments;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.nafanya.vkdocs.presentation.ui.AudioPlayer;

public class MusicPlayFragment extends Fragment {
    private AudioPlayer player;

    public static MusicPlayFragment newInstance() {
        return null;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        player = new AudioPlayer(getActivity().getApplication(), "");
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
