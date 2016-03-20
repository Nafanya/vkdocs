package io.github.nafanya.vkdocs.presentation.ui;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

/**
 * Created by pva701 on 21.03.16.
 */
public class AudioPlayer {

    private MediaPlayer player;

    public AudioPlayer(Context app, String uri) {
        player = new MediaPlayer();
        Uri playUri = Uri.parse(uri);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            player.setDataSource(app, playUri);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        }
    }

    public void stop() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    public void play() {
        player.setOnCompletionListener(mp -> stop());

        player.start();
    }
}
