package io.github.nafanya.vkdocs.presentation.ui.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.felipecsl.gifimageview.library.GifImageView;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.net.impl.download.DownloadRequest;
import io.github.nafanya.vkdocs.presentation.presenter.DocumentViewerPresenter;
import io.github.nafanya.vkdocs.presentation.services.AudioPlayerService;
import timber.log.Timber;

/**
 * Created by nafanya on 3/21/16.
 */
public class GifImageFragment extends Fragment implements DocumentViewerPresenter.Callback {
    public static final String GIF_KEY = "gif_key";

    private VkDocument document;

    @Bind(R.id.gifImageView)
    GifImageView gifImageView;

    @Bind(R.id.gif_play)
    ImageButton playButton;

    private DocumentViewerPresenter presenter;

    public static GifImageFragment newInstance(VkDocument document) {
        GifImageFragment fragment = new GifImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(GIF_KEY, document);
        fragment.setArguments(args);
        return fragment;
    }


    @OnClick(R.id.gif_play)
    public void onClickPlay(View v) {
        gifImageView.startAnimation();
        playButton.setVisibility(View.GONE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        document = getArguments().getParcelable(GIF_KEY);

        App app = (App)getActivity().getApplication();
        presenter = new DocumentViewerPresenter(
                app.getEventBus(),
                app.getRepository(),
                app.getDownloadManager(),
                app.getAppCacheRoot(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_gif, container, false);

        ButterKnife.bind(this, rootView);

        return rootView;
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

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        presenter.openDocument(document);
    }

    private byte[] readFile(String file) throws IOException {
        return readFile(new File(file));
    }

    private byte[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long length = f.length();
            if (length > Integer.MAX_VALUE) {
                throw new IOException("File size >= 2 GB");
            }
            byte[] data = new byte[(int)length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }

    @Override
    public void onOpenDocument(VkDocument document) {
        Timber.d("cache = " + document.isCached() + " off = " + document.isOffline() + " path = " + document.getPath());
        final byte[] gif;
        try {
            gif = readFile(document.getPath());
        } catch (IOException ignored) {
            return;
        }
        gifImageView.setBytes(gif);
    }

    @Override
    public void onAlreadyDownloading(VkDocument document, boolean isReallyAlreadyDownloading) {
        document.getRequest().addListener(new DownloadRequest.RequestListener() {
            @Override
            public void onProgress(int percentage) {
                Timber.d("PROGRESS = " + percentage);
            }

            @Override
            public void onComplete() {
                onOpenDocument(document);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
}
