package io.github.nafanya.vkdocs.presentation.ui.views.fragments.documents;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.SortMode;
import io.github.nafanya.vkdocs.presentation.ui.adapters.base.EmptyRecyclerView;
import io.github.nafanya.vkdocs.presentation.ui.decorators.EndOffsetItemDecorator;
import io.github.nafanya.vkdocs.presentation.ui.decorators.SimpleDivierItermDecorator;

/**
 * Created by nafanya on 3/25/16.
 */
public abstract class DocumentsListBaseFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemSelectedListener {
    public static final String OFFLNE_KEY = "offlne_key";
    public static final String DOC_TYPE_KEY = "doc_type_key";

    protected boolean isOffline;
    protected VkDocument.ExtType documentType;
    // TODO: [fragment] add to instance creator fields
    protected SortMode sortMode = SortMode.DATE;
    protected String searchFilter = "";

    protected boolean isRefreshing;

    @Bind(R.id.list_documents) EmptyRecyclerView recyclerView;
    @Bind(R.id.empty_view) View emptyView;
    @Bind(R.id.swipe_container) SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isOffline = getArguments().getBoolean(OFFLNE_KEY);
        documentType = getArguments().getParcelable(DOC_TYPE_KEY);
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
