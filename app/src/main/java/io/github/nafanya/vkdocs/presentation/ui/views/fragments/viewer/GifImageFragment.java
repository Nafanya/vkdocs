package io.github.nafanya.vkdocs.presentation.ui.views.fragments.viewer;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
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
import timber.log.Timber;

/**
 * Created by nafanya on 3/21/16.
 */
public class GifImageFragment extends AbstractViewerFragment implements DocumentViewerPresenter.Callback {
    public static final String GIF_KEY = "gif_key";

    private VkDocument document;
    private GifDrawable gif;
    private DocumentViewerPresenter presenter;

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
//            GlideProgressListener.removeGlideProgressListener(listener);
        }
    };

    @Bind(R.id.imageView)
    ImageView imageView;

    @Bind(R.id.progressBar)
    CircularProgressBar progressBar;

    public static GifImageFragment newInstance(VkDocument document, boolean isFirst) {
        GifImageFragment fragment = new GifImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(GIF_KEY, document);
        args.putBoolean(FIRST_KEY, isFirst);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        document = getArguments().getParcelable(GIF_KEY);
        //Timber.d("IS CACHED %s: %b", document.title, document.isCached());

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
        if (gif != null) {
            gif.start();
        }
        super.onResume();
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
        presenter.onStop();
        release();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    private void release() {
//        GlideProgressListener.removeGlideProgressListener(listener);
        if (gif != null) {
            gif = null;
        }
    }

    @Override
    public void onBecameVisible() {
        Timber.d("on became visible");
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
        Timber.d("cached = " + document.isCached() + " off = " + document.isOffline() + " path = " + document.getPath());
        Glide.with(this)
                .load(Uri.fromFile(new File(document.getPath())))
                .asGif()
                .into(target);
    }

    @Override
    public void onProgress(int progress) {
        progressBar.setProgress(progress);
    }

    @Override
    public void onError(Exception e) {
        //TODO write here
    }
}
