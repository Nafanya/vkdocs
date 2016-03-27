package io.github.nafanya.vkdocs.presentation.ui.fragments.documents;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.SortMode;
import io.github.nafanya.vkdocs.presentation.ui.adapters.base.EmptyRecyclerView;
import io.github.nafanya.vkdocs.presentation.ui.decorators.DividerItemDecorator;
import io.github.nafanya.vkdocs.presentation.ui.decorators.EndOffsetItemDecorator;

/**
 * Created by nafanya on 3/25/16.
 */
public abstract class DocumentsListBaseFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener {

    public static final String OFFLNE_KEY = "offlne_key";
    public static final String EXT_TYPE_KEY = "ext_type_key";
    public static final String SORT_MODE_KEY = "sort_mode_key";
    //public static final String SEARCH_QUERY_KEY = "search_query_key";

    protected boolean isOffline;
    protected VkDocument.ExtType documentType;
    protected SortMode sortMode = SortMode.DATE;
    protected String searchQuery = "";

    protected boolean isRefreshing;

    @Bind(R.id.list_documents) EmptyRecyclerView recyclerView;
    @Bind(R.id.empty_view) View emptyView;
    @Bind(R.id.empty_view_image) ImageView emptyViewImage;
    @Bind(R.id.empty_view_text) TextView emptyViewText;
    @Bind(R.id.swipe_container) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.rootLayout) RelativeLayout rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            isOffline = getArguments().getBoolean(OFFLNE_KEY);
            documentType = (VkDocument.ExtType) getArguments().getSerializable(EXT_TYPE_KEY);
            sortMode = (SortMode) getArguments().getSerializable(SORT_MODE_KEY);
            ////searchQuery = getArguments().getString(SEARCH_QUERY_KEY);
        }

        if (savedInstanceState != null) {
            isOffline = savedInstanceState.getBoolean(OFFLNE_KEY);
            documentType = (VkDocument.ExtType) savedInstanceState.getSerializable(EXT_TYPE_KEY);
            sortMode = (SortMode) savedInstanceState.getSerializable(SORT_MODE_KEY);
            //searchQuery = savedInstanceState.getString(SEARCH_QUERY_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putBoolean(OFFLNE_KEY, isOffline);
        state.putSerializable(EXT_TYPE_KEY, documentType);
        state.putSerializable(SORT_MODE_KEY, sortMode);
        //state.putString(SEARCH_QUERY_KEY, searchQuery);
        super.onSaveInstanceState(state);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_document_list, container, false);
        ButterKnife.bind(this, rootView);


        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setupEmptyView();
        initRecyclerView();
        initSwipeRefreshLayout();
    }

    protected void setupEmptyView() {
        if (documentType == null) {
            emptyViewImage.setImageResource(R.drawable.ic_folder);
            emptyViewText.setText(R.string.no_documents);
            return;
        }
        switch (documentType) {
            case ARCHIVE:
                emptyViewImage.setImageResource(R.drawable.zip_box);
                emptyViewText.setText(R.string.no_archives); break;
            case AUDIO:
                emptyViewImage.setImageResource(R.drawable.music_box);
                emptyViewText.setText(R.string.no_audios); break;
            case VIDEO:
                emptyViewImage.setImageResource(R.drawable.movie_box);
                emptyViewText.setText(R.string.no_videos); break;
            case GIF:
                emptyViewImage.setImageResource(R.drawable.image_vintage);
                emptyViewText.setText(R.string.no_gifs); break;
            case IMAGE:
                emptyViewImage.setImageResource(R.drawable.image_box);
                emptyViewText.setText(R.string.no_images); break;
            case BOOK: case TEXT:
                emptyViewImage.setImageResource(R.drawable.book);
                emptyViewText.setText(R.string.no_books); break;
            case UNKNOWN:
                emptyViewImage.setImageResource(R.drawable.file_multiple);
                emptyViewText.setText(R.string.no_others); break;
        }
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecorator(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setEmptyView(emptyView);
        // Convert dp to px
        final int px = (int) (this.getResources().getDimension(R.dimen.recyclerview_bottom_padding) * getResources().getDisplayMetrics().density);
        recyclerView.addItemDecoration(new EndOffsetItemDecorator(px));
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    protected void setRefresh(boolean isRef) {
        isRefreshing = isRef;
        //swipeRefreshLayout.setRefreshing(isRefreshing);
        swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(isRefreshing));
    }
}
