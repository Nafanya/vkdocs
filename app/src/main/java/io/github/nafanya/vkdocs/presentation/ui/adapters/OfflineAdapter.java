package io.github.nafanya.vkdocs.presentation.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.net.impl.download.DownloadRequest;
import io.github.nafanya.vkdocs.presentation.ui.SortMode;
import io.github.nafanya.vkdocs.presentation.ui.adapters.base.BaseSortedAdapter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.base.CommonItemEventListener;
import io.github.nafanya.vkdocs.utils.DocumentComparator;
import io.github.nafanya.vkdocs.utils.FileFormatter;

public class OfflineAdapter extends BaseSortedAdapter {
    private static final int DOCUMENT_STATE_NORMAL = 0;
    private static final int DOCUMENT_STATE_DOWNLOADING = 1;
    private ItemEventListener listener;

    public OfflineAdapter(Context context, FileFormatter fileFormatter, SortMode sortMode, ItemEventListener listener) {
        super(context, fileFormatter, sortMode);
        this.listener = listener;

    }

    public void setData(List<VkDocument> documents) {
        super.setData(documents);
        Collections.sort(documents, DocumentComparator.offlineComparator(sortMode));
        notifyDataSetChanged();
    }

    public List<VkDocument> getData() {
        return documents;
    }

    public void setSortMode(SortMode sortMode) {
        this.sortMode = sortMode;
        Collections.sort(documents, DocumentComparator.offlineComparator(sortMode));
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
//        Timber.d("[offlineadapter] pos: %d, title: %s", position, documents.get(position).title);
        if (documents.get(position).isOfflineInProgress())
            return DOCUMENT_STATE_DOWNLOADING;
        return DOCUMENT_STATE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        Timber.d("[offlineadapter] viewtype: %d", viewType);
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_document, parent, false);
        if (viewType == DOCUMENT_STATE_NORMAL) {
            return new DocumentViewHolder(view, listener);
        } else {
            return new DownloadingDocViewHolder(view, listener);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DownloadingDocViewHolder) {
            setListener(position, ((DownloadingDocViewHolder) holder).setup(position, documents.get(position)));
        } else {
            ((DocumentViewHolder) holder).setup(position, documents.get(position));
        }
    }

    private void moveItem(int fromPosition, int toPosition) {
        VkDocument model = documents.remove(fromPosition);
        documents.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public class DownloadingDocViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.ic_document_type)
        ImageView documentTypeIcon;

        @Bind(R.id.ic_document_offline)
        ImageView documentOfflineIcon;
        @Bind(R.id.ic_document_offline_progress)
        ImageView documentOfflineInProgressIcon;

        @Bind(R.id.ic_document_cached)
        ImageView documentCachedIcon;
        @Bind(R.id.ic_document_cache_progress)
        ImageView documentCacheInProgressIcon;

        @Bind(R.id.text_document_title)
        TextView title;

        @Bind(R.id.statusLabels)
        TextView size;

        @Bind(R.id.sortLabel)
        TextView sortLabel;

        @Bind(R.id.progress)
        ProgressBar downloadProgress;

        @Bind(R.id.buttonContextMenu)
        ImageButton buttonContext;

        @Bind(R.id.buttonCancel)
        ImageButton buttonCancel;

        private ItemEventListener listener;

        public DownloadingDocViewHolder(View view, ItemEventListener listener) {
            super(view);
            this.listener = listener;

            ButterKnife.bind(this, view);

            documentOfflineIcon.setVisibility(View.GONE);
            documentOfflineInProgressIcon.setVisibility(View.GONE);
            documentCachedIcon.setVisibility(View.GONE);
            documentCacheInProgressIcon.setVisibility(View.GONE);

            downloadProgress.setVisibility(View.VISIBLE);
            sortLabel.setVisibility(View.GONE);

            view.setOnClickListener(this);
            buttonContext.setOnClickListener(this);
            buttonCancel.setOnClickListener(this);
        }

        private class ProgressListener implements DownloadRequest.RequestListener {
            private int position;
            private VkDocument doc;
            public ProgressListener(int position, VkDocument doc) {
                this.position = position;
                this.doc = doc;
            }

            @Override
            public void onProgress(int percentage) {
                //Timber.d("adapter on update: " + percentage + ", title = " + doc.title);
                size.setText(fileFormatter.formatFrom(doc.getRequest()));
                downloadProgress.setProgress(percentage);
            }

            @Override
            public void onComplete() {
//                Timber.d("[adapter] onComplete");
//                Timber.d("on complete doc = " + doc + ", request = " + doc.getRequest());
                doc.setPath(doc.getRequest().getDest());
                doc.resetRequest();
                listener.onCompleteDownloading(getAdapterPosition(), doc);
                notifyItemChanged(position);
                Comparator<VkDocument> comparator = DocumentComparator.offlineComparator(sortMode);
                int toPosition = documents.size() - 1;
                for (int i = position; i < documents.size(); ++i)
                    if (comparator.compare(doc, documents.get(i)) < 0) {
                        toPosition = i - 1;
                        break;
                    }
                removeListener(position);
                if (position != toPosition)
                    moveItem(position, toPosition);

            }

            @Override
            public void onError(Exception e) {
            }
        }

        public DownloadRequest.RequestListener setup(int position, VkDocument doc) {
            documentTypeIcon.setImageDrawable(fileFormatter.getIcon(doc, context));
            title.setText(doc.title);

            downloadProgress.setProgress(fileFormatter.getProgress(doc.getRequest()));
            size.setText(fileFormatter.formatFrom(doc.getRequest()));
            if (doc.isDownloading()) {
                buttonContext.setVisibility(View.GONE);
                buttonCancel.setVisibility(View.VISIBLE);
            } else {
                buttonContext.setVisibility(View.VISIBLE);
                buttonCancel.setVisibility(View.GONE);
            }
            return new ProgressListener(position, doc);
        }

        @Override
        public void onClick(View v) {
            if (listener == null)
                return;
            int pos = getAdapterPosition();
            if (v == buttonCancel) {
                listener.onCancelDownloading(pos, documents.get(pos));
            } else if (v == buttonContext) {
                listener.onClickContextMenu(pos, documents.get(pos));
            } else {
                listener.onClick(pos, documents.get(pos));
            }
        }
    }

    public interface ItemEventListener extends CommonItemEventListener {
        void onCancelDownloading(int position, VkDocument document);
        void onCompleteDownloading(int position, VkDocument document);
    }
}
