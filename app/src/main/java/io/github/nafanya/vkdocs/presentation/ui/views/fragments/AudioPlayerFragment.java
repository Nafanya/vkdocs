package io.github.nafanya.vkdocs.presentation.ui.views.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.media.AudioPlayerService;
import io.github.nafanya.vkdocs.presentation.ui.media.CustomMediaPlayer;
import io.github.nafanya.vkdocs.presentation.ui.views.fragments.base.OnPageChanged;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class AudioPlayerFragment extends Fragment
        implements OnPageChanged, AudioPlayerService.OnPrepared {
    public static String MUSIC_KEY = "music_key";

    @Bind(R.id.seek_bar)
    SeekBar seekBar;

    @Bind(R.id.prev_button)
    ImageView prevButton;

    @Bind(R.id.play_button)
    ImageView playButton;

    @Bind(R.id.pause_button)
    ImageView pauseButton;

    @Bind(R.id.next_button)
    ImageView nextButton;

    public interface AudioPlayerControl {
        void nextAudio();
        void prevAudio();
    }

    @OnClick(R.id.next_button)
    void onClickNext(View v) {
        callback.nextAudio();
    }

    @OnClick(R.id.prev_button)
    void onClickPrev(View v) {
        callback.prevAudio();
    }

    @OnClick(R.id.play_button)
    void onClickPlay(View v) {
        if (isPlayerInitialized()) {
            playButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.VISIBLE);
            playerService.resume();
        }
    }

    @OnClick(R.id.pause_button)
    void onClickPause(View v) {
        if (isPlayerInitialized()) {
            pauseButton.setVisibility(View.GONE);
            playButton.setVisibility(View.VISIBLE);
            playerService.pause();
        }
    }

    private VkDocument audioDocument;
    private AudioPlayerService playerService;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playerService = ((AudioPlayerService.AudioPlayerBinder) service).service();
            if (isBecameVisible)
                startPlaying();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playerService = null;
        }
    };

    public static AudioPlayerFragment newInstance(VkDocument document) {
        AudioPlayerFragment fragment = new AudioPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MUSIC_KEY, document);
        fragment.setArguments(bundle);
        return fragment;
    }

    private AudioPlayerControl callback;

    public boolean isPlayerInitialized() {
        return playerService != null;
    }

    private boolean isBecameVisible = false;
    private boolean isAlreadyNotifiedAboutVisible = false;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            callback = (AudioPlayerControl) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement AudioPlayerControl");
        }

        Intent intent = new Intent(activity, AudioPlayerService.class);
        activity.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioDocument = getArguments().getParcelable(MUSIC_KEY);
    }

    private Subscription subscription = Subscriptions.empty();
    private void startPlaying() {
        Timber.d("ON START AUDIO " + audioDocument.title);
        if (!playerService.isNowPlaying(audioDocument)) {
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
        seekBar.setMax(100);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isPlayerInitialized() && playerService.isPrepared() && fromUser) {
                    int toTime = (int) (playerService.getDuration() * progress / 100.0);
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
        return rootView;
    }

    private void enableControlButton(boolean enable) {
        seekBar.setEnabled(enable);
        if (!enable)
            seekBar.setProgress(0);
        playButton.setEnabled(enable);
        pauseButton.setEnabled(enable);
    }

    @Override
    public void onPlayerPrepared() {
        enableControlButton(true);
    }

    private class SeekBarUpdater extends CustomMediaPlayer.PlayingListener {
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser != isBecameVisible) {
            isBecameVisible = isVisibleToUser;
            if (isBecameVisible) {
                if (isResumed())
                    onBecameVisible();
            } else
                onBecameInvisible();
        }
    }

    @Override
    public void onBecameVisible() {
        if (isAlreadyNotifiedAboutVisible)
            return;
        enableControlButton(false);
        isAlreadyNotifiedAboutVisible = true;
        if (isPlayerInitialized())
            startPlaying();
    }

    @Override
    public void onBecameInvisible() {
        isAlreadyNotifiedAboutVisible = false;
        if (isPlayerInitialized())
            playerService.stop();
        enableControlButton(false);
        subscription.unsubscribe();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (subscription.isUnsubscribed() && isPlayerInitialized())
            subscription = playerService.setPlayingListener(new SeekBarUpdater());
        if (isBecameVisible)
            onBecameVisible();
    }

    @Override
    public void onPause() {
        subscription.unsubscribe();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        getActivity().unbindService(serviceConnection);
        super.onDestroy();
    }
}
