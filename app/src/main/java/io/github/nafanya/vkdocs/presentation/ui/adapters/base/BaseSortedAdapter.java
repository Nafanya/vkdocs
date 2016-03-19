package io.github.nafanya.vkdocs.presentation.ui.adapters.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.SortMode;
import io.github.nafanya.vkdocs.utils.FileFormatter;
import timber.log.Timber;

public abstract class BaseSortedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected List<VkDocument> documents;
    protected FileFormatter fileFormatter;
    protected SortMode sortMode;
    protected Context context;

    public BaseSortedAdapter(Context context, FileFormatter fileFormatter, SortMode sortMode) {
        this.context = context;
        this.fileFormatter = fileFormatter;
        this.sortMode = sortMode;
    }

    @Override
    public long getItemId(int position) {
        return documents.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public class DocumentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.ic_document_type)
        ImageView documentTypeIcon;
        @Bind(R.id.ic_document_offline)
        ImageView documentOfflineIcon;
        @Bind(R.id.ic_document_offline_progress)
        ImageView documentOfflineInProgressIcon;
        @Bind(R.id.extraMenu)
        ImageButton contextMenu;
        @Bind(R.id.text_document_title)
        TextView title;
        @Bind(R.id.sortLabel) TextView sortLabel; // Only size yet
        @Bind(R.id.statusLabels) TextView statusLables; // Only size yet

        private CommonItemEventListener listener;

        public DocumentViewHolder(View view, CommonItemEventListener listener) {
            super(view);
            this.listener = listener;

            ButterKnife.bind(this, view);
            view.setOnClickListener(this);

            contextMenu.setOnClickListener(this);
        }

        public void setup(VkDocument doc) {
            contextMenu.setOnClickListener(this);

            title.setText(doc.title);

            documentTypeIcon.setImageDrawable(fileFormatter.getIcon(doc, context));

            documentOfflineIcon.setVisibility(View.GONE);
            documentOfflineInProgressIcon.setVisibility(View.GONE);

            if (doc.isOffline()) {
                documentOfflineIcon.setVisibility(View.VISIBLE);
            } else if (doc.isOfflineInProgress()) {
                documentOfflineInProgressIcon.setVisibility(View.VISIBLE);
            }

            final int sortLabelText;
            final String statusLabelText;
            switch (sortMode) {
                case DATE:
                    sortLabelText = R.string.label_modified;
                    statusLabelText = fileFormatter.formatDate(doc.date);
                    break;
                case SIZE:case NAME:
                    sortLabelText = R.string.label_size;
                    statusLabelText = fileFormatter.formatSize(doc.size);
                    break;
                default:
                    sortLabelText = R.string.label_size;
                    statusLabelText = fileFormatter.formatSize(doc.size);
            }

            sortLabel.setText(sortLabelText);
            statusLables.setText(statusLabelText);
        }

        @Override
        public void onClick(View v) {
            if (listener == null)
                return;

            int pos = getAdapterPosition();
            if (v.getId() == contextMenu.getId()) {
                listener.onClickContextMenu(pos, documents.get(pos));
                Timber.d("Clicked context for #%d" , pos);
            } else if (v.getId() == itemView.getId()) {
                listener.onClick(pos, documents.get(pos));
                Timber.d("Clicked item #%d, %s" , pos, documents.get(pos).title);
            }
        }
    }

    public abstract void setData(List<VkDocument> documents);
    public abstract void setSortMode(SortMode sortMode);
    public void removeIndex(int position) {
        documents.remove(position);
        notifyDataSetChanged();
    }
    public List<VkDocument> getData() {
        return documents;
    }

}
