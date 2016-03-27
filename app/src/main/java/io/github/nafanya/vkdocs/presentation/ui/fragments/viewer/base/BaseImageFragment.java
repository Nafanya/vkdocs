package io.github.nafanya.vkdocs.presentation.ui.fragments.viewer.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;

/**
 * Created by pva701 on 26.03.16.
 */
public abstract class BaseImageFragment extends DownloadableDocFragment {

    @Bind(R.id.image_container)
    RelativeLayout layout;

    @Bind(R.id.imageView)
    public ImageView imageView;

    @Bind(R.id.progressBar)
    public CircularProgressBar progressBar;

    @Override
    public View rootForSnackbar() {
        return layout;
    }

    protected Snackbar errorOpening;

    protected void errorWithOpening() {
        progressBar.setVisibility(View.GONE);
        errorOpening = Snackbar
                .make(rootForSnackbar(), "Problem with opening", Snackbar.LENGTH_INDEFINITE);
        errorOpening.setActionTextColor(Color.YELLOW);
        showSnackbar(errorOpening);
    }


    @Override
    public void onBecameVisible() {
        super.onBecameVisible();
        presenter.openDocument();
    }

    @Override
    public void onBecameInvisible() {
        super.onBecameInvisible();
        if (errorOpening != null)
            errorOpening.dismiss();
        if (presenter.isDownloading()) {
            presenter.cancelDownloading();
            progressBar.setProgress(0);
        }
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
    public void onProgress(int progress) {
        hideErrorSnackbar();
        progressBar.setProgress(progress);
    }

    @Override
    public void onStop() {
        release();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    protected void release() {

    }
}
