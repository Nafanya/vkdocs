package io.github.nafanya.vkdocs.presentation.ui.views.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.interactor.GetDocuments;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.presenter.DocumentViewerPresenter;
import io.github.nafanya.vkdocs.presentation.ui.views.activities.DocumentViewerActivity;
import uk.co.senab.photoview.PhotoViewAttacher;


public class ImageFragment extends Fragment
        implements DocumentViewerPresenter.Callback, DocumentViewerActivity.OnPageChanged{
    public static final String IMAGE_KEY = "image_key";
    private DocumentViewerPresenter presenter;

    private VkDocument document;
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

    };

    @Bind(R.id.imageView)
    ImageView imageView;

    @Bind(R.id.progressBar)
    CircularProgressBar progressBar;

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

        App app = (App)getActivity().getApplication();
        presenter = new DocumentViewerPresenter(
                app.getEventBus(),
                app.getRepository(),
                app.getCacheManager(), this);
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
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        attacher = new PhotoViewAttacher(imageView);
        if (isBecameVisible)
            onBecameVisible();
    }

    @Override
    public void onPause() {
        super.onPause();
        release();
    }

    @Override
    public void onStop() {
        presenter.onStop();
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

    private boolean isBecameVisible = false;
    private boolean isAlreadyNotifiedAboutVisible = false;

    @Override
    public void onBecameVisible() {
        if (isAlreadyNotifiedAboutVisible)
            return;
        isAlreadyNotifiedAboutVisible = true;
        presenter.openDocument(document);
    }

    @Override
    public void onBecameInvisible() {
        isAlreadyNotifiedAboutVisible = false;
        if (presenter.isDownloading()) {
            presenter.cancelDownloading();
            progressBar.setProgress(0);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser != isBecameVisible) {
            isBecameVisible = isVisibleToUser;
            if (isBecameVisible) {
                if (isResumed())
                    onBecameVisible();
            } else
                onBecameInvisible();
        }
    }

    @Override
    public void onCompleteCaching(VkDocument document) {
        Glide.with(this)
                .load(Uri.fromFile(new File(document.getPath())))
                .into(target);
    }

    @Override
    public void onProgress(int progress) {
        progressBar.setProgress(progress);
    }

    @Override
    public void onError(Exception e) {

    }
}
