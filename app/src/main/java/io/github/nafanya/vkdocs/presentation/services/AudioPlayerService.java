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

import io.github.nafanya.vkdocs.domain.model.VkDocument;
import timber.log.Timber;

/**
 * Created by pva701 on 21.03.16.
 */
public class AudioPlayerService extends Service implements MediaPlayer.OnPreparedListener {

    private MediaPlayer mediaPlayer;
    private Uri playingUri;

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
        Timber.d("play url = " + document.url);
        return Uri.parse(document.url);
    }

    public boolean isNowPlaying(VkDocument document) {
        return playingUri != null && playingUri.equals(getUri(document));
    }

    public void play(VkDocument document) throws IOException {
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
        onDestroy();//holy7 release media player
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mediaPlayer != null)
            mediaPlayer.start();
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
}
