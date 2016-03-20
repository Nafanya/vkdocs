package io.github.nafanya.vkdocs.presentation.ui;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

/**
 * Created by pva701 on 21.03.16.
 */
public class AudioPlayer {

    private MediaPlayer player;

    public AudioPlayer(Context app, String uri) {
        //Uri playUri = Uri.parse("file:///sdcard/Songs/ARR Hits/hosannatamil.mp3");
        Uri playUri = Uri.parse(uri);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            player.setDataSource(app, playUri);
        } catch (IllegalArgumentException e) {
            //Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (SecurityException e) {
            //Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (IllegalStateException e) {
            //Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    public void stop() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    public void play() {
        stop();

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop();
            }
        });

        player.start();
    }

}
