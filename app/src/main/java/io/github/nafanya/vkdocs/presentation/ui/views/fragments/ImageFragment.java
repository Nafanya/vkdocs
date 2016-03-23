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
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by nafanya on 3/22/16.
 */
public class ImageFragment extends Fragment {
    public static final String IMAGE_KEY = "image_key";

    private VkDocument document;
    private PhotoViewAttacher attacher;
    private SimpleTarget target = new SimpleTarget<GlideBitmapDrawable>() {
        @Override
        public void onResourceReady(GlideBitmapDrawable bitmap, GlideAnimation glideAnimation) {
//            GlideProgressListener.removeGlideProgressListener(listener);
            imageView.setImageBitmap(bitmap.getBitmap());
            if (attacher != null) {
                attacher.update();
            }
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {
//            GlideProgressListener.removeGlideProgressListener(listener);
        }
    };

    @Bind(R.id.imageView)
    ImageView imageView;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

//    private ProgressListener listener = (bytesRead, contentLength, done) -> {
//        progressBar.setProgress((int) (100 * bytesRead / contentLength));
//        Timber.d("Image load] %d/%d b; %d/%d kb", bytesRead, contentLength, bytesRead/1024, contentLength/1024);
//    };

    public static ImageFragment newInstance(VkDocument document) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(IMAGE_KEY, document);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        document = getArguments().getParcelable(IMAGE_KEY);
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
        attacher = new PhotoViewAttacher(imageView);
    }

    @Override
    public void onPause() {
        super.onPause();
        release();
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
//        GlideProgressListener.removeGlideProgressListener(listener);
        if (attacher != null) {
            attacher.cleanup();
            attacher = null;
        }
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        GlideProgressListener.addGlideProgressListener(listener);
        if (document.isCached() || document.isOffline()) {
            Glide
                    .with(this)
                    .load(Uri.fromFile(new File(document.getPath())))
                    .into(target);
        } else {
            Glide
                    .with(this)
                    .load(document.url)
                    .into(target);
        }
    }

}
