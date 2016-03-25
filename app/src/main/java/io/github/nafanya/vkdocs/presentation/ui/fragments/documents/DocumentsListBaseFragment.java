package io.github.nafanya.vkdocs.presentation.ui.fragments.documents;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.SortMode;
import io.github.nafanya.vkdocs.presentation.ui.adapters.base.BaseSortedAdapter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.base.EmptyRecyclerView;
import io.github.nafanya.vkdocs.presentation.ui.decorators.EndOffsetItemDecorator;
import io.github.nafanya.vkdocs.presentation.ui.decorators.SimpleDivierItermDecorator;
import timber.log.Timber;

/**
 * Created by nafanya on 3/25/16.
 */
public abstract class DocumentsListBaseFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener {

    public static final String OFFLNE_KEY = "offlne_key";
    public static final String DOC_TYPE_KEY = "doc_type_key";
    public static final String SORT_MODE_KEY = "sort_mode_key";
    public static final String SEARCH_QUERY_KEY = "search_query_key";

    protected BaseSortedAdapter adapter;

    protected boolean isOffline;
    protected VkDocument.ExtType documentType;
    protected SortMode sortMode = SortMode.DATE;
    protected String searchQuery = "";

    protected boolean isRefreshing;

    @Bind(R.id.list_documents) EmptyRecyclerView recyclerView;
    @Bind(R.id.empty_view) View emptyView;
    @Bind(R.id.swipe_container) SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isOffline = getArguments().getBoolean(OFFLNE_KEY);
        documentType = (VkDocument.ExtType) getArguments().getSerializable(DOC_TYPE_KEY);
        sortMode = (SortMode) getArguments().getSerializable(SORT_MODE_KEY);
        searchQuery = getArguments().getString(SEARCH_QUERY_KEY);
        Timber.d("[Fragment] restored doctype: %s", documentType);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putBoolean(OFFLNE_KEY, isOffline);
        state.putSerializable(DOC_TYPE_KEY, documentType);
        state.putSerializable(SORT_MODE_KEY, sortMode);
        state.putString(SEARCH_QUERY_KEY, searchQuery);
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
        initRecyclerView();
        initSwipeRefreshLayout();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new SimpleDivierItermDecorator(getActivity()));
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
