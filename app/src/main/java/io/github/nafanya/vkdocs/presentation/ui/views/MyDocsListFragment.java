package io.github.nafanya.vkdocs.presentation.ui.views;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.data.exceptions.VKException;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.ExtDocFilter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.MyDocsAdapter;
import io.github.nafanya.vkdocs.presentation.ui.decorators.SimpleDivierItermDecorator;
import io.github.nafanya.vkdocs.presentation.ui.views.base.AbstractListFragment;
import timber.log.Timber;

public class MyDocsListFragment extends AbstractListFragment<MyDocsAdapter> implements DocumentsPresenter.Callback, MyDocsAdapter.ItemEventListener {

    @Bind(R.id.list_documents)
    RecyclerView recyclerView;

    public static MyDocsListFragment newInstance(VkDocument.ExtType type) {
        Bundle bundle = new Bundle();
        if (type == null)
            bundle.putSerializable(EXT_TYPE_KEY, ALL);
        else
            bundle.putSerializable(EXT_TYPE_KEY, new ExtDocFilter(type));
        MyDocsListFragment fragment = new MyDocsListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public MyDocsAdapter newAdapter() {
        App app = (App)getActivity().getApplication();
        return new MyDocsAdapter(((MainActivity)getActivity()).getFileFormatter(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_documents_list, container, false);

        ButterKnife.bind(this, rootView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new SimpleDivierItermDecorator(getActivity()));
        presenter.setCallback(this);
        presenter.getDocuments();

        return rootView;
    }

    @Override
    public void onGetDocuments(List<VkDocument> documents) {
        if (adapter == null)
            adapter = newAdapter();
        adapter.setData(documents);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onMakeOffline(Exception ex) {
        //TODO write here
    }

    @Override
    public void onRename(Exception ex) {
        //TODO write here
    }

    @Override
    public void onDelete(Exception ex) {
        //TODO write here
    }

    @Override
    public void onClickContextMenu(int position, VkDocument document) {
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
        dialog.setContentView(R.layout.dialog_bottom);
        dialog.show();
    }

    @Override
    public void onClickMakeOffline(int position, VkDocument document) {
        presenter.makeOffline(document);
    }

    @Override
    public void onNetworkError(Exception ex) {
        Timber.d("network error" + ((VKException)ex).getVkError().toString());
    }

    @Override
    public void onDatabaseError(Exception ex) {
        Timber.d("db error");
    }
}
