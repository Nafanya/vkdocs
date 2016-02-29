package io.github.nafanya.vkdocs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by pva701 on 28.02.16.
 */
public class OneFragment extends Fragment {

    private String title;

    @Bind(R.id.text_title)
    TextView text;

    public OneFragment() {
        // Required empty public constructor
    }

    public static Fragment createFragment(String title) {
        Bundle args = new Bundle();
        args.putString("title", title);
        Fragment fragment = new OneFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            title = getArguments().getString("title");
        } else {
            title = "Stub";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);

        ButterKnife.bind(this, view);
        text.setText(title);

        return view;
    }

}
