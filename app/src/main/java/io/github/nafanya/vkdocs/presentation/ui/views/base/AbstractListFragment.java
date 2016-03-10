package io.github.nafanya.vkdocs.presentation.ui.views.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.vk.sdk.api.model.VKApiDocument;

import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.DocFilter;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.ExtDocFilter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.base.CommonItemEventListener;
import timber.log.Timber;

public abstract class AbstractListFragment<AdapterType>
        extends Fragment implements DocumentsPresenter.Callback, CommonItemEventListener {
    public static String EXT_TYPE_KEY = "ext_types_key";

    public static DocFilter ALL = new ExtDocFilter(
                    VkDocument.ExtType.TEXT,
                    VkDocument.ExtType.BOOK,
                    VkDocument.ExtType.ARCHIVE,
                    VkDocument.ExtType.GIF,
                    VkDocument.ExtType.IMAGE,
                    VkDocument.ExtType.AUDIO,
                    VkDocument.ExtType.VIDEO,
                    VkDocument.ExtType.UNKNOWN);

    protected DocumentsPresenter presenter;
    protected AdapterType adapter;

    protected abstract AdapterType newAdapter();

    protected DocumentsPresenter newPresenter() {
        App app = (App)getActivity().getApplication();

        DocFilter filter = (DocFilter) getArguments().get(EXT_TYPE_KEY);
        return new DocumentsPresenter(
                filter,
                app.getEventBus(),
                app.getRepository(),
                app.getDownloadManager(),
                this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("ON CREATE");
        presenter = newPresenter();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void onClick(int position, VKApiDocument document) {
        //TODO write here
    }
}
