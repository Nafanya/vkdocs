package io.github.nafanya.vkdocs.presentation.ui.adapters.base;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.SortMode;

public abstract class AbstractAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public abstract void setData(List<VkDocument> documents);
    public abstract void setSortMode(SortMode sortMode);
    public abstract List<VkDocument> getData();
}
