package io.github.nafanya.vkdocs.presentation.glide.listener;

/**
 * Created by Alexey on 06.12.15.
 */
public interface ProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}