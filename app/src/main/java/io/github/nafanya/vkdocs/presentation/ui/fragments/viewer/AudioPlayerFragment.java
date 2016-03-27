package io.github.nafanya.vkdocs.presentation.ui.fragments.viewer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.fragments.viewer.base.BaseViewerFragment;
import io.github.nafanya.vkdocs.presentation.ui.media.AudioPlayerService;
import io.github.nafanya.vkdocs.presentation.ui.media.CustomMediaPlayer;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class AudioPlayerFragment extends BaseViewerFragment implements AudioPlayerService.OnPrepared {
    public static String MUSIC_KEY = "music_key";

    @Bind(R.id.seek_bar)
    SeekBar seekBar;

    @Bind(R.id.play_button)
    ImageView playButton;

    @Bind(R.id.pause_button)
    ImageView pauseButton;

    @Bind(R.id.file_name)
    TextView fileName;

    @OnClick(R.id.play_button)
    void onClickPlay(View v) {
        if (playerService.isCompleted())
            playerService.seekTo(0);
        playButton.setVisibility(View.GONE);
        pauseButton.setVisibility(View.VISIBLE);
        playerService.resume();
    }

    @OnClick(R.id.pause_button)
    void onClickPause(View v) {
        pauseButton.setVisibility(View.GONE);
        playButton.setVisibility(View.VISIBLE);
        playerService.pause();
    }

    private VkDocument audioDocument;
    private AudioPlayerService playerService;

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
        App app = (App)activity.getApplicationContext();
        playerService = app.getPlayerService();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioDocument = getArguments().getParcelable(MUSIC_KEY);
        Timber.d("on create " + this);
    }

    private Subscription subscription = Subscriptions.empty();
    private void startPlaying() {
        Timber.d("ON START AUDIO " + audioDocument.title);
        if (!playerService.isNowInPlayer(audioDocument)) {
            try {
                playerService.play(audioDocument, this);
            } catch (IOException ignore) {
                //TODO wtf7
            }
        }

        subscription = playerService.setPlayingListener(new SeekBarUpdater());
    }

    //TODO block seekbar until starting playing
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_audio_player, null);
        ButterKnife.bind(this, rootView);
        seekBar.setMax(CustomMediaPlayer.PERCENTAGE);
        fileName.setText(audioDocument.title);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (playerService.isPrepared() && fromUser) {
                    int toTime = playerService.getDuration() * progress / CustomMediaPlayer.PERCENTAGE;
                    playerService.seekTo(toTime);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        if (!playerService.isNowInPlayer(audioDocument))
            setEnabledControls(false);
        return rootView;
    }

    private void setEnabledControls(boolean enable) {
        seekBar.setEnabled(enable);
        playButton.setEnabled(enable);
        pauseButton.setEnabled(enable);
    }

    private void resetViewToInitialState() {
        seekBar.setProgress(0);
        playButton.setVisibility(View.GONE);
        pauseButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPlayerPrepared() {
        setEnabledControls(true);
    }

    private class SeekBarUpdater extends CustomMediaPlayer.PlayingListener {
        /**
         * Progress callback
         ***/
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(Integer integer) {
            seekBar.setProgress(integer);
            if (integer == CustomMediaPlayer.PERCENTAGE) {
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBecameVisible() {
        super.onBecameVisible();

        if (playerService.isNowInPlayer(audioDocument)) {
            if (playerService.isPlaying()) {
                playButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);
            } else {
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.GONE);
            }
        } else
            setEnabledControls(false);
        startPlaying();
    }

    @Override
    public void onBecameInvisible() {
        super.onBecameInvisible();
        playerService.stop();
        subscription.unsubscribe();
        resetViewToInitialState();
        setEnabledControls(false);
    }

    @Override
    public void onResume() {
        if (subscription.isUnsubscribed())
            subscription = playerService.setPlayingListener(new SeekBarUpdater());
        super.onResume();
    }

    @Override
    public void onPause() {
        subscription.unsubscribe();
        super.onPause();
    }
}
