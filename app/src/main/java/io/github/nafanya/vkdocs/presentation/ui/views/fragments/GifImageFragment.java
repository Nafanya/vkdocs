package io.github.nafanya.vkdocs.presentation.ui.views.fragments;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.glide.listener.GlideProgressListener;
import io.github.nafanya.vkdocs.presentation.glide.listener.ProgressListener;

/**
 * Created by nafanya on 3/21/16.
 */
public class GifImageFragment extends Fragment {
    public static final String GIF_KEY = "gif_key";

    private VkDocument document;
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
            GlideProgressListener.removeGlideProgressListener(listener);
        }
    };

    @Bind(R.id.imageView)
    ImageView imageView;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private ProgressListener listener = (bytesRead, contentLength, done) -> {
        progressBar.setProgress((int) (100 * bytesRead / contentLength));
    };

    public static GifImageFragment newInstance(VkDocument document) {
        GifImageFragment fragment = new GifImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(GIF_KEY, document);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        document = getArguments().getParcelable(GIF_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_image, container, false);

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint() && gif != null) {
            gif.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (gif != null) {
            gif.stop();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    private void release() {
        GlideProgressListener.removeGlideProgressListener(listener);
        if (gif != null) {
            gif = null;
        }
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        GlideProgressListener.addGlideProgressListener(listener);
        if (document.isCached() || document.isOffline()) {
            Glide
                    .with(this)
                    .load(Uri.fromFile(new File(document.getPath())))
                    .asGif()
                    .into(target);
        } else {
            Glide
                    .with(this)
                    .load(document.url)
                    .asGif()
                    .into(target);
        }
    }

}
