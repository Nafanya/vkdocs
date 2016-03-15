package io.github.nafanya.vkdocs.presentation.ui.views.documents;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.DocFilter;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.ExtDocFilter;
import io.github.nafanya.vkdocs.presentation.ui.SortMode;
import io.github.nafanya.vkdocs.presentation.ui.adapters.MyDocsAdapter;
import io.github.nafanya.vkdocs.presentation.ui.decorators.EndOffsetItemDecorator;
import io.github.nafanya.vkdocs.presentation.ui.decorators.SimpleDivierItermDecorator;
import io.github.nafanya.vkdocs.presentation.ui.dialogs.SortByDialogFragment;

/**
 * Created by nafanya on 3/15/16.
 */
public abstract class BaseDocumentsFragment extends Fragment implements MyDocsAdapter.ItemEventListener,
        SortByDialogFragment.Callback {

    public static final String ARG_DOC_TYPE = "arg_doc_type";
    public static final String ARG_SORT_MODE = "arg_sort_mode";

    protected SortMode sortMode;
    protected DocFilter filter;

    @Bind(R.id.list_documents) RecyclerView recyclerView;
    protected MyDocsAdapter adapter;

    public BaseDocumentsFragment() {
        // Required empty public constructor
    }

    public static DocumentsFragment newInstance(VkDocument.ExtType type, @NonNull SortMode sortMode) {
        Bundle bundle = new Bundle();
        if (type == null) {
            bundle.putSerializable(ARG_DOC_TYPE, ExtDocFilter.ALL);
        } else {
            bundle.putSerializable(ARG_DOC_TYPE, new ExtDocFilter(type));
        }
        bundle.putSerializable(ARG_SORT_MODE, sortMode);
        DocumentsFragment fragment = new DocumentsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        App app = (App) getActivity().getApplication();
        filter = (DocFilter) getArguments().get(ARG_DOC_TYPE);
        sortMode = (SortMode) getArguments().get(ARG_SORT_MODE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_documents_list, container, false);

        ButterKnife.bind(this, root);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new SimpleDivierItermDecorator(getActivity()));
        // Convert dp to px
        final int px = (int) (getActivity().getResources().getDimension(R.dimen.recyclerview_bottom_padding) * getActivity().getResources().getDisplayMetrics().density);
        recyclerView.addItemDecoration(new EndOffsetItemDecorator(px));

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.documents_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by:
                showSortByDialog(sortMode);
                return true;
            case R.id.action_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSortByDialog(SortMode currentSortMode) {
        DialogFragment dialog = SortByDialogFragment.create(currentSortMode);
        dialog.setTargetFragment(this, 0);
        dialog.show(getActivity().getSupportFragmentManager(), "sortmode");
    }

    @Override
    public void onClick(int position, VkDocument document) {

    }

    @Override
    public void onClickContextMenu(int position, VkDocument document) {

    }

    @Override
    public void onSortModeChanged(SortMode newSortMode) {
        if (newSortMode != sortMode) {
            sortMode = newSortMode;
            adapter.setSortMode(sortMode);
        }
    }

    public SortMode getSortMode() {
        return sortMode;
    }
}
