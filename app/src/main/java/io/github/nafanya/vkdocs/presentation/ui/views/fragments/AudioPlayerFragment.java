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
import android.widget.SeekBar;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.services.AudioPlayerService;
import io.github.nafanya.vkdocs.presentation.ui.MediaControlImpl;
import rx.Subscription;
import timber.log.Timber;

public class AudioPlayerFragment extends Fragment
        implements MediaPlayer.OnPreparedListener {
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


    private Subscription subscription;

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

        subscription = playerService.subscribe(new SeekBarUpdater());
    }

    @Override
    public void onStop() {
        subscription.unsubscribe();
        super.onStop();
    }

    private MediaPlayer player;

    @Override
    public void onPrepared(MediaPlayer mp) {
        player = mp;
        player.setOnSeekCompleteListener(mp1 ->
                playerService.setFuckingMediaPlayerPosition(player.getCurrentPosition()));
    }

    @Bind(R.id.seek_bar)
    SeekBar seekBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.audio_player_fragment, null);
        ButterKnife.bind(this, rootView);
        seekBar.setMax(100);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (player != null && fromUser) {
                    Timber.d("dur = " + player.getDuration());
                    int toTime = (int) (player.getDuration() * progress / 100.0);
                    Timber.d("TO TIME = " + toTime / 1000 + " WITH PERC = " + progress);
                    Timber.d("CUR POS0 = " + player.getCurrentPosition());
                    player.seekTo(toTime);
                    playerService.setRealPosition(player.getCurrentPosition());
                    Timber.d("CUR POS1 = " + player.getCurrentPosition());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return rootView;
    }

    private class SeekBarUpdater extends AudioPlayerService.PlayingListener {
        /**Progress callback***/
        @Override
        public void onCompleted() {
            Timber.d("on complete seek bar");
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(Integer integer) {
            Timber.d("seek bar progress = " + integer);
            seekBar.setProgress(integer);
        }
    }
}
