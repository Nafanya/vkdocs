package io.github.nafanya.vkdocs.presentation.ui.fragments.viewer;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;

import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.fragments.viewer.base.BaseImageFragment;
import timber.log.Timber;

/**
 * Created by nafanya on 3/21/16.
 */
public class GifImageFragment extends BaseImageFragment {

    private GifDrawable gif;

    private SimpleTarget target = new SimpleTarget<GifDrawable>() {
        @Override
        public void onResourceReady(GifDrawable gifDrawable, GlideAnimation glideAnimation) {
            gif = gifDrawable;
            imageView.setImageDrawable(gif.getCurrent());
            gif.start();
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {
            errorWithOpening();
        }
    };

    public static GifImageFragment newInstance(VkDocument document) {
        GifImageFragment fragment = new GifImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(DOC_KEY, document);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        if (gif != null)
            gif.start();

        super.onResume();//it is important!!!
    }

    @Override
    public void onPause() {
        super.onPause();
        if (gif != null)
            gif.stop();
    }

    @Override
    protected void release() {
        if (gif != null)
            gif = null;
    }

    @Override
    public void onCompleteCaching(VkDocument document) {
        Timber.d("cached = " + document.isCached() + " off = " + document.isOffline() + " path = " + document.getPath());
        Glide.with(this)
                .load(Uri.fromFile(new File(document.getPath())))
                .asGif()
                .into(target);
    }
}
