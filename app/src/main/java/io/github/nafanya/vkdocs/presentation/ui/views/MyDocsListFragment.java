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
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.DocFilter;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.ExtDocFilter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.MyDocsAdapter;
import io.github.nafanya.vkdocs.presentation.ui.decorators.SimpleDivierItermDecorator;
import io.github.nafanya.vkdocs.presentation.ui.views.base.AbstractListFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.dialogs.BottomMenu;
import timber.log.Timber;

public class MyDocsListFragment extends AbstractListFragment<MyDocsAdapter>
        implements DocumentsPresenter.Callback,
        MyDocsAdapter.ItemEventListener, BottomMenu.MenuEventListener {

    public static DocFilter ALL = new ExtDocFilter(
            VkDocument.ExtType.TEXT,
            VkDocument.ExtType.BOOK,
            VkDocument.ExtType.ARCHIVE,
            VkDocument.ExtType.GIF,
            VkDocument.ExtType.IMAGE,
            VkDocument.ExtType.AUDIO,
            VkDocument.ExtType.VIDEO,
            VkDocument.ExtType.UNKNOWN);


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
        return new MyDocsAdapter(getActivity(), app.getFileFormatter(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_documents_list, container, false);

        ButterKnife.bind(this, rootView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new SimpleDivierItermDecorator(getActivity()));
        presenter.setCallback(this);
        presenter.getDocuments();
        Timber.d("on create view");
        return rootView;
    }

    /***Presenter events***/
    @Override
    public void onGetDocuments(List<VkDocument> documents) {
        Timber.d("on get docs!");
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
    public void onNetworkError(Exception ex) {
        Timber.d("network error" + ((VKException)ex).getVkError().toString());
    }

    @Override
    public void onDatabaseError(Exception ex) {
        Timber.d("db error");
    }


    /***Adapter events***/
    @Override
    public void onClickContextMenu(int position, VkDocument document) {
        App app = (App)getActivity().getApplication();
        BottomSheetDialog dialog = new BottomMenu(getActivity(), document, app.getFileFormatter(), this);
        dialog.show();
    }


    /***Menu events***/
    @Override
    public void onClickMakeOffline(VkDocument document, boolean isMakeOffline) {
        Timber.d("on make offline " + document.title + " is make off = " + isMakeOffline);
        if (isMakeOffline)
            presenter.makeOffline(document);
    }
}
