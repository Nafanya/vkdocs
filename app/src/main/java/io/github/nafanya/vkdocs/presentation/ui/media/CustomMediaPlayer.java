package io.github.nafanya.vkdocs.presentation.ui.media;

import android.media.MediaPlayer;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class CustomMediaPlayer extends MediaPlayer implements
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private volatile int fuckingMediaPlayerPosition;
    private volatile int realPosition;
    private volatile boolean invalidState = false;
    private volatile int currentSessionId = -1;
    private volatile boolean isPrepared = false;
    private volatile boolean isCompleted = false;

    private ConnectableObservable<Integer> progressObserver;
    private OnPreparedListener preparedListener;

    public CustomMediaPlayer() {
        setOnSeekCompleteListener(this);
        setOnCompletionListener(this);
        super.setOnPreparedListener(this);
        setOnErrorListener(this);
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    private volatile int sessionError = -1;
    private PlayingException lastException;

    public static class PlayingException extends RuntimeException {
        private int what;
        private int extra;
        public PlayingException(int what, int extra) {
            this.what = what;
            this.extra = extra;
        }

        public int getWhat() {
            return what;
        }

        public int getExtra() {
            return extra;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        sessionError = currentSessionId;
        lastException = new PlayingException(what, extra);
        progressObserver.connect();
        return true;
    }

    public static abstract class PlayingListener extends Subscriber<Integer> {

    }

    public Subscription setPlayingListener(PlayingListener s) {
        return progressObserver.subscribe(s);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        isCompleted = true;
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener listener) {
        preparedListener = listener;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Timber.d("On prep custom media player");
        isPrepared = true;
        progressObserver.connect();
        if (preparedListener != null)
            preparedListener.onPrepared(mp);
    }

    @Override
    public void seekTo(int msec) throws IllegalStateException {
        super.seekTo(msec);
        isCompleted = false;
        realPosition = getCurrentPosition();
        invalidState = true;
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        fuckingMediaPlayerPosition = getCurrentPosition();
        invalidState = false;
    }

    public void initAudioSession(int sessionId) {
        isCompleted = false;
        currentSessionId = sessionId;
    }

    @Override
    public void prepareAsync() {
        progressObserver = ConnectableObservable.create(new PlayingThread(currentSessionId)).
                subscribeOn(Schedulers.computation()).
                observeOn(AndroidSchedulers.mainThread()).replay(1);
        Timber.d("prepare async");
        super.prepareAsync();
    }

    @Override
    public void stop() throws IllegalStateException {
        currentSessionId = -1;
        invalidState = false;
        isPrepared = false;
        isCompleted = false;
        super.stop();
    }

    private class PlayingThread implements Observable.OnSubscribe<Integer> {
        private int sessionId;
        private PlayingThread(int sessionId) {
            this.sessionId = sessionId;
        }

        @Override
        public void call(Subscriber<? super Integer> subscriber) {
            int prevPerc = -1;
            int duration = getDuration();
            Timber.d("IN THREAD PLA");
            while (sessionId == currentSessionId && sessionError != currentSessionId) {
                if (!invalidState) {
                    int diff = getCurrentPosition() - fuckingMediaPlayerPosition;
                    int perc = realPosition + diff;
                    if (perc > duration)
                        perc = duration;

                    if (isCompleted)
                        perc = duration;

                    if (prevPerc != perc)
                        subscriber.onNext(perc);
                    prevPerc = perc;
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignore) {}
            }
            Timber.d("ERROR PLAYING THREAD");
            if (sessionError == currentSessionId)
                subscriber.onError(lastException);
            else
                subscriber.onCompleted();
        }
    }
}
