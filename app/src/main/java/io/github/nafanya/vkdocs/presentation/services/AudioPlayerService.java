package io.github.nafanya.vkdocs.presentation.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.nafanya.vkdocs.domain.model.VkDocument;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by pva701 on 21.03.16.
 */
public class AudioPlayerService extends Service implements MediaPlayer.OnPreparedListener {

    private MediaPlayer mediaPlayer;
    private Uri playingUri;
    private MediaPlayer.OnPreparedListener listener;
    private volatile int currentSessionId = -1;
    private ConnectableObservable<Integer> progressObserver;


    private volatile int fuckingMediaPlayerPosition;
    private volatile int realPosition;
    private volatile boolean isFuckingPositionSet = true;

    public void setRealPosition(int realPosition) {
        this.realPosition = realPosition;
        isFuckingPositionSet = false;
    }

    public void setFuckingMediaPlayerPosition(int fuckingMediaPlayerPosition) {
        this.fuckingMediaPlayerPosition = fuckingMediaPlayerPosition;
        isFuckingPositionSet = true;
    }



    public class AudioPlayerBinder extends Binder {
        public io.github.nafanya.vkdocs.presentation.services.AudioPlayerService service() {
            return io.github.nafanya.vkdocs.presentation.services.AudioPlayerService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.d("on start AudioPlayerService");
        return START_STICKY;
    }

    private Uri getUri(VkDocument document) {
        if (document.getPath() != null)
            return Uri.parse(document.getPath());
        return Uri.parse(document.url);
    }

    public boolean isNowPlaying(VkDocument document) {
        return playingUri != null && playingUri.equals(getUri(document));
    }

    public void play(VkDocument document) throws IOException {
        play(document, null);
    }

    public void play(VkDocument document, MediaPlayer.OnPreparedListener listener) throws IOException {
        currentSessionId = document.getId();
        progressObserver = ConnectableObservable.create(new PlayingThread(currentSessionId)).
                subscribeOn(Schedulers.computation()).
                observeOn(AndroidSchedulers.mainThread()).replay(1);

        this.listener = listener;
        Timber.d("url = " + document.url);
        playingUri = getUri(document);
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } else {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        mediaPlayer.setDataSource(getApplicationContext(), playingUri);
        mediaPlayer.setOnPreparedListener(this);
        Timber.d("[prepare]");
        mediaPlayer.prepareAsync();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void resume() {
        if (!mediaPlayer.isPlaying())
            mediaPlayer.start();
    }

    public void stop() {
        currentSessionId = -1;
        onDestroy();//release media playerService
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        progressObserver.connect();
        Timber.d("ON PREPARED!!");
        if (mediaPlayer != null) {
            Timber.d("play!!!");
            mediaPlayer.start();
        }

        if (listener != null)
            listener.onPrepared(mp);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new AudioPlayerBinder();
    }

    @Override
    public void onDestroy() {
        playingUri = null;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private class PlayingThread implements Observable.OnSubscribe<Integer> {
        private int sessionId;
        private PlayingThread(int sessionId) {
            this.sessionId = sessionId;
        }

        @Override
        public void call(Subscriber<? super Integer> subscriber) {
            int prevPerc = -1;
            int duration = mediaPlayer.getDuration();
            Timber.d("DUDDDD = " + duration);
            while (mediaPlayer != null && sessionId == currentSessionId) {
                if (isFuckingPositionSet) {
                    int diff = mediaPlayer.getCurrentPosition() - fuckingMediaPlayerPosition;
                    int perc = (int) (100 * ((realPosition + diff) * 1.0 / duration));
                    if (prevPerc != perc) {
                        Timber.d("POSITION = " + mediaPlayer.getCurrentPosition());
                        subscriber.onNext(perc);
                    }
                    prevPerc = perc;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignore) {}
            }
            subscriber.onCompleted();
        }
    }

    public Subscription subscribe(PlayingListener s) {
        return progressObserver.subscribe(s);
    }

    public static abstract class PlayingListener extends Subscriber<Integer> {
    }
}
