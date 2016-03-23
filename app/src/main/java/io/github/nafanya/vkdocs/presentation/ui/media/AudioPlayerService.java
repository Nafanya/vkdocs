package io.github.nafanya.vkdocs.presentation.ui.media;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

import io.github.nafanya.vkdocs.domain.model.VkDocument;
import rx.Subscription;
import timber.log.Timber;

public class AudioPlayerService extends Service implements MediaPlayer.OnPreparedListener {

    private CustomMediaPlayer mediaPlayer;
    private Uri playingUri;
    private MediaPlayer.OnPreparedListener listener;

    public class AudioPlayerBinder extends Binder {
        public AudioPlayerService service() {
            return AudioPlayerService.this;
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
        //this.listener = listener;
        playingUri = getUri(document);
        if (mediaPlayer == null) {
            mediaPlayer = new CustomMediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } else {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        mediaPlayer.setDataSource(getApplicationContext(), playingUri);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.initAudioSession(document.getId());
        mediaPlayer.prepareAsync();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void resume() {
        if (!mediaPlayer.isPlaying())
            mediaPlayer.start();
    }

    public void seekTo(int msec) {
        mediaPlayer.seekTo(msec);
    }

    public boolean isPrepared() {
        return mediaPlayer.isPrepared();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public void stop() {
        Timber.d("stooop = " + playingUri);
        onDestroy();//release media playerService
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        Timber.d("ON PREPARED!!");
        if (mediaPlayer != null)
            mediaPlayer.start();

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
        Timber.d("ON DESTORY, on release");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    public Subscription setPlayingListener(CustomMediaPlayer.PlayingListener l) {
        return mediaPlayer.setPlayingListener(l);
    }
}
