package io.github.nafanya.vkdocs.presentation.ui.views.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.views.activities.DocumentViewerActivity;
import timber.log.Timber;

public class VideoPlayerFragment extends Fragment implements DocumentViewerActivity.OnPageChanged {

    public static String VIDEO_KEY = "video_key";
    public static String FIRST_KEY = "first_key";

    @Bind(R.id.video_view)
    VideoView videoView;

    private VkDocument videoDocument;
    private boolean isThisFirstFragment;
    private MediaController videoControl;

    public static VideoPlayerFragment newInstance(VkDocument document, boolean isThisFirst) {
        VideoPlayerFragment fragment = new VideoPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(VIDEO_KEY, document);
        bundle.putBoolean(FIRST_KEY, isThisFirst);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoDocument = getArguments().getParcelable(VIDEO_KEY);
        isThisFirstFragment = getArguments().getBoolean(FIRST_KEY);
        videoControl = new MediaController(getActivity());
        //setRetainInstance(true);//holy shit
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_video_player, null);
        ButterKnife.bind(this, rootView);

        Uri uri = videoDocument.getPath() != null ?
                Uri.parse(videoDocument.getPath()) :
                Uri.parse(videoDocument.url);

        videoView.setVideoURI(uri);
        videoView.setMediaController(videoControl);
        return rootView;
    }

    @Override
    public void onBecameVisible() {
        Timber.d("visible " + this);
        videoView.start();
    }

    @Override
    public void onBecameInvisible() {
        Timber.d("invisible " + this);
        videoView.stopPlayback();
    }

    @Override
    public void onResume() {
        super.onResume();
        videoControl.setAnchorView(videoView);

        Timber.d("resume " + this);
        if (isThisFirstFragment) {
            Timber.d("is this first fr");
            isThisFirstFragment = false;
            onBecameVisible();
        }
    }

    @Override
    public void onPause() {
        videoView.pause();
        super.onPause();
    }

    @Override
    public void onSetFirst(boolean isFirst) {
        isThisFirstFragment = isFirst;
    }
}
