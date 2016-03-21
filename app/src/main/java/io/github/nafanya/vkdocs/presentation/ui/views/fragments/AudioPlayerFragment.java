package io.github.nafanya.vkdocs.presentation.ui.views.fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;

import java.io.IOException;

import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.services.AudioPlayerService;
import io.github.nafanya.vkdocs.presentation.ui.MediaControlImpl;
import timber.log.Timber;

public class AudioPlayerFragment extends Fragment implements MediaPlayer.OnPreparedListener {
    public static String MUSIC_KEY = "music_key";

    private VkDocument audioDocument;
    private AudioPlayerService playerService;
    private MediaController mediaController;
    private Handler handler = new Handler();

    public interface Player {
        AudioPlayerService playerService();
    }

    public static AudioPlayerFragment newInstance(VkDocument document) {
        AudioPlayerFragment fragment = new AudioPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MUSIC_KEY, document);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        Timber.d("ON ATTACH");
        try {
            playerService = ((Player) activity).playerService();//get playerService for pause, resume
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
        if (!playerService.isNowPlaying(audioDocument)) {
            try {
                playerService.play(audioDocument, this);
            } catch (IOException ignore) {
                //TODO wtf7
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaController.setMediaPlayer(new MediaControlImpl(mp));

        handler.post(() -> {
            mediaController.setEnabled(true);
            mediaController.show(0);
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.audio_player_fragment, null);

        mediaController = new MediaController(getActivity());
        mediaController.setAnchorView(rootView);

        return rootView;
    }
}
