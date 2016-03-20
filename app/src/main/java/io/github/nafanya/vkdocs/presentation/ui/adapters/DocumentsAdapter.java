package io.github.nafanya.vkdocs.presentation.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.SortMode;
import io.github.nafanya.vkdocs.presentation.ui.adapters.base.BaseSortedAdapter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.base.CommonItemEventListener;
import io.github.nafanya.vkdocs.utils.DocumentComparator;
import io.github.nafanya.vkdocs.utils.FileFormatter;

public class DocumentsAdapter extends BaseSortedAdapter {
    private CommonItemEventListener listener;

    public DocumentsAdapter(Context context, FileFormatter fileFormatter, SortMode sortMode, CommonItemEventListener listener) {
        super(context, fileFormatter, sortMode);
        this.context = context;
        this.listener = listener;
    }

    @Override
    public DocumentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_document, parent, false);
        return new DocumentViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder hold, int position) {
        DocumentViewHolder holder = (DocumentViewHolder)hold;
        holder.setup(documents.get(position));
    }

    public void setSortMode(SortMode sortMode) {
        this.sortMode = sortMode;
        Collections.sort(documents, DocumentComparator.getComparator(sortMode));
        notifyDataSetChanged();
    }

    @Override
    public void setData(List<VkDocument> documents) {
        super.setData(documents);
        Collections.sort(documents, DocumentComparator.getComparator(sortMode));
        notifyDataSetChanged();
    }
}
