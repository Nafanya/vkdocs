package io.github.nafanya.vkdocs.presentation.ui.fragments.viewer.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;

import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.presenter.DocumentViewerPresenter;

/**
 * Created by pva701 on 26.03.16.
 */
public abstract class DownloadableDocFragment extends BaseViewerFragment implements DocumentViewerPresenter.Callback {

    public static final String DOC_KEY = "doc_key";
    protected VkDocument document;
    protected DocumentViewerPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        document = getArguments().getParcelable(DOC_KEY);

        App app = (App)getActivity().getApplication();
        presenter = new DocumentViewerPresenter(
                app.getEventBus(),
                app.getRepository(),
                app.getCacheManager(), document, this);

    }

    private Snackbar errorSnackbar;

    @Override
    public void onError(Exception e) {
        errorSnackbar = Snackbar
                .make(rootForSnackbar(), "No internet connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", view -> {
                    presenter.retryOpen();
                });
        errorSnackbar.setActionTextColor(Color.YELLOW);
        showSnackbar(errorSnackbar);
    }

    @Override
    public void onBecameInvisible() {
        hideErrorSnackbar();
        if (presenter.isDownloading())
            presenter.cancelDownloading();
        super.onBecameInvisible();
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void onStop() {
        presenter.onStop();
        super.onStop();
    }

    protected void hideErrorSnackbar() {
        if (errorSnackbar != null)
            errorSnackbar.dismiss();
    }

    @Override
    public void onReleaseResources() {
        hideErrorSnackbar();
        super.onReleaseResources();
    }
}
