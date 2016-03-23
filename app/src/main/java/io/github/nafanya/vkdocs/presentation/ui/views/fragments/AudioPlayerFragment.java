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
import io.github.nafanya.vkdocs.presentation.ui.views.activities.DocumentViewerActivity;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class AudioPlayerFragment extends Fragment implements DocumentViewerActivity.OnPageChanged {
    public static String MUSIC_KEY = "music_key";
    public static String FIRST_KEY = "first_key";

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
    private boolean isThisFirstFragment;
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

    public static AudioPlayerFragment newInstance(VkDocument document, boolean isThisFirst) {
        AudioPlayerFragment fragment = new AudioPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MUSIC_KEY, document);
        bundle.putBoolean(FIRST_KEY, isThisFirst);
        fragment.setArguments(bundle);
        return fragment;
    }

    public boolean isPlayerInitialized() {
        return playerService != null;
    }

    private boolean isBecameVisible = false;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        Intent intent = new Intent(activity, AudioPlayerService.class);
        activity.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioDocument = getArguments().getParcelable(MUSIC_KEY);
        isThisFirstFragment = getArguments().getBoolean(FIRST_KEY);
    }

    private Subscription subscription = Subscriptions.empty();

    private void startPlaying() {
        Timber.d("ON START AUDIO " + audioDocument.title + " playerService = " + playerService);
        if (!playerService.isNowPlaying(audioDocument)) {
            if (seekBar != null)
                seekBar.setProgress(0);
            try {
                playerService.play(audioDocument);
            } catch (IOException ignore) {
                //TODO wtf7
            }
        }

        subscription = playerService.setPlayingListener(new SeekBarUpdater());
    }

    @Override
    public void onBecameVisible() {
        isBecameVisible = true;
        if (isPlayerInitialized())
            startPlaying();
    }

    @Override
    public void onBecameInvisible() {
        Timber.d("ON STOP AUDIO " + audioDocument.title);
        isBecameVisible = false;
        if (isPlayerInitialized())
            playerService.stop();
        subscription.unsubscribe();
    }

    @Override
    public void onSetFirst(boolean isFirst) {
        Timber.d("on set first!!");
        isThisFirstFragment = isFirst;
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
    public void onResume() {
        Timber.d("on resume!!!");
        super.onResume();
        if (subscription.isUnsubscribed() && isPlayerInitialized())
            subscription = playerService.setPlayingListener(new SeekBarUpdater());

        if (isThisFirstFragment) {//it is hack!!!
            isThisFirstFragment = false;
            onBecameVisible();
        }
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
