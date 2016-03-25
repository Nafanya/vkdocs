package io.github.nafanya.vkdocs.presentation.ui.views.fragments.viewer;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import uk.co.senab.photoview.PhotoViewAttacher;


public class ImageFragment extends AbstractViewerFragment implements DocumentViewerPresenter.Callback {
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

    public static ImageFragment newInstance(VkDocument document, boolean isFirst) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(IMAGE_KEY, document);
        args.putBoolean(FIRST_KEY, isFirst);
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

    @Override
    public void onBecameVisible() {
        document = GetDocuments.getDocument(document);
        presenter.openDocument(document);
    }

    @Override
    public void onBecameInvisible() {
        if (presenter.isDownloading())
            presenter.cancelDownloading();//fix it7
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
