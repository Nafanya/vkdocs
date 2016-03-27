package io.github.nafanya.vkdocs.presentation.ui.fragments.viewer;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;

import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.fragments.viewer.base.BaseImageFragment;
import uk.co.senab.photoview.PhotoViewAttacher;


public class ImageFragment extends BaseImageFragment {

    private PhotoViewAttacher attacher;
    private SimpleTarget target = new SimpleTarget<GlideBitmapDrawable>() {
        @Override
        public void onResourceReady(GlideBitmapDrawable bitmap, GlideAnimation glideAnimation) {
            imageView.setImageBitmap(bitmap.getBitmap());
            if (attacher != null) {
                attacher.update();
            }
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {
            errorWithOpening();
        }
    };

    public static ImageFragment newInstance(VkDocument document) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(DOC_KEY, document);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        attacher = new PhotoViewAttacher(imageView);
        super.onResume();//important!!!
    }


    protected void release() {
        if (attacher != null) {
            attacher.cleanup();
            attacher = null;
        }
    }

    @Override
    public void onCompleteCaching(VkDocument document) {
        Glide.with(this)
                .load(Uri.fromFile(new File(document.getPath())))
                .into(target);
    }
}
