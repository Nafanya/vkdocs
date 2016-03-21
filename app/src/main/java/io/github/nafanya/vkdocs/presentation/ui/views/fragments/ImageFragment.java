package io.github.nafanya.vkdocs.presentation.ui.views.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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

    @Bind(R.id.imageView)
    ImageView imageView;

    @Bind(R.id.progress)
    ProgressBar progressBar;

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
        attacher = new PhotoViewAttacher(imageView);

        return rootView;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (document.isOffline() || document.isCached()) {
            File file = new File(document.getPath());
            imageView.setImageURI(Uri.fromFile(file));
            progressBar.setVisibility(View.GONE);
        } else {
            Picasso.with(getActivity()).load(document.url).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                    attacher.update();
                }

                @Override
                public void onError() {

                }
            });
        }
    }
}
