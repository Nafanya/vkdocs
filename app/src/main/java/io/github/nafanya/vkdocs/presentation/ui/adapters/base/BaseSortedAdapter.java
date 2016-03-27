package io.github.nafanya.vkdocs.presentation.ui.adapters.base;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.net.impl.download.DownloadRequest;
import io.github.nafanya.vkdocs.presentation.ui.SortMode;
import io.github.nafanya.vkdocs.utils.FileFormatter;
import timber.log.Timber;

public abstract class BaseSortedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected List<VkDocument> documents;
    protected List<VkDocument> documentsOriginal;
    protected FileFormatter fileFormatter;
    protected SortMode sortMode;
    protected String searchFilter;
    protected Context context;

    private List<DownloadRequest> requests;
    private List<DownloadRequest.RequestListener> listeners;

    private void initializeRequestsAndListeners() {
        requests = new ArrayList<>(documents.size());
        listeners = new ArrayList<>(documents.size());
        for (int i = 0; i < documents.size(); ++i) {
            requests.add(documents.get(i).getRequest());
            listeners.add(null);
        }
    }

    private void resetRequestsAndListeners() {
        if (listeners != null) {
            for (int i = 0; i < requests.size(); ++i) {
                if (requests.get(i) != null)
                    requests.get(i).removeListener(listeners.get(i));
                }
        }
    }

    private RecyclerView.AdapterDataObserver dataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            resetRequestsAndListeners();
            initializeRequestsAndListeners();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if (itemCount != 1)
                throw new IllegalStateException("itemCount != 1");
            if (requests.get(positionStart) != null)
                requests.get(positionStart).removeListener(listeners.get(positionStart));
            requests.remove(positionStart);
            listeners.remove(positionStart);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            if (itemCount != 1)
                throw new IllegalStateException("itemCount != 1");
            if (fromPosition == toPosition)
                return;
            DownloadRequest req = requests.get(fromPosition);
            DownloadRequest.RequestListener lis = listeners.get(fromPosition);
            requests.remove(fromPosition);
            listeners.remove(fromPosition);
            requests.add(toPosition, req);
            listeners.add(toPosition, lis);
        }
    };

    public BaseSortedAdapter(Context context, FileFormatter fileFormatter, SortMode sortMode) {
        this.context = context;
        this.fileFormatter = fileFormatter;
        this.sortMode = sortMode;
        registerAdapterDataObserver(dataObserver);
    }

    @Override
    public long getItemId(int position) {
        return documents.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    protected void setListener(int position, DownloadRequest.RequestListener listener) {
        if (listener == null)
            return;
        if (listeners.get(position) != null)
            requests.get(position).removeListener(listeners.get(position));
        requests.get(position).addListener(listener);
        listeners.set(position, listener);
    }

    protected void setRequest(int position, DownloadRequest request) {
        if (listeners.get(position) != null && requests.get(position) != null)
            requests.get(position).removeListener(listeners.get(position));
        requests.set(position, request);
    }

    protected void removeListener(int position) {
        setListener(position, null);
    }

    public class DocumentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.ic_document_type)
        ImageView documentTypeIcon;

        @Bind(R.id.ic_document_offline)
        ImageView documentOfflineIcon;
        @Bind(R.id.ic_document_offline_progress)
        ImageView documentOfflineInProgressIcon;


        @Bind(R.id.buttonContextMenu)
        ImageButton contextMenu;
        @Bind(R.id.text_document_title)
        TextView title;
        @Bind(R.id.sortLabel) TextView sortLabel;
        @Bind(R.id.statusLabels) TextView statusLables;

        AnimationDrawable downloadAnimation;

        private CommonItemEventListener listener;

        public DocumentViewHolder(View view, CommonItemEventListener listener) {
            super(view);
            this.listener = listener;

            ButterKnife.bind(this, view);
            documentOfflineInProgressIcon.setBackgroundResource(R.drawable.anim_download_progress);
            downloadAnimation = (AnimationDrawable) documentOfflineInProgressIcon.getBackground();
            view.setOnClickListener(this);

            contextMenu.setOnClickListener(this);
        }

        public DownloadRequest.RequestListener setup(int position, VkDocument doc) {
            setRequest(position, doc.getRequest());

            downloadAnimation.stop();

            contextMenu.setOnClickListener(this);
            title.setText(doc.title);
            documentTypeIcon.setImageDrawable(fileFormatter.getIcon(doc, context));

            documentOfflineIcon.setVisibility(View.GONE);
            documentOfflineInProgressIcon.setVisibility(View.GONE);

            if (doc.isOffline()) {
                documentOfflineIcon.setVisibility(View.VISIBLE);
            } else if (doc.isOfflineInProgress()) {
                documentOfflineInProgressIcon.setVisibility(View.VISIBLE);
                downloadAnimation.start();
            }

            final int sortLabelText;
            final String statusLabelText;
            switch (sortMode) {
                case DATE:
                    sortLabelText = R.string.label_added;
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

            if (doc.isOfflineInProgress())
                return new OnCompleteOfflineListener(position, doc);
            return null;
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

        private class OnCompleteOfflineListener implements DownloadRequest.RequestListener {
            private int position;
            private VkDocument document;
            public OnCompleteOfflineListener(int position, VkDocument document) {
                this.position = position;
                this.document = document;
            }

            @Override
            public void onProgress(int percentage) {

            }

            @Override
            public void onComplete() {
                document.resetRequest();
                removeListener(position);
                notifyItemChanged(position);
                downloadAnimation.stop();
            }

            @Override
            public void onError(Exception e) {
                downloadAnimation.stop();
            }
        };
    }

    private void onlySetFilter(String filter) {
        if (filter == null)
            filter = "";
        searchFilter = filter;
        if (documents == null || documentsOriginal == null)
            return;

        documents = Stream.of(documentsOriginal)
                .filter(doc -> doc.title.toLowerCase().contains(searchFilter.toLowerCase()))
                .filter(x -> x != null)
                .collect(Collectors.toList());
    }

    public void setSearchFilter(String filter) {
        onlySetFilter(filter);
        notifyDataSetChanged();
    }

    public abstract Comparator<VkDocument> getComparator();

    public void setData(List<VkDocument> docs, String searchQuery, SortMode sortMode) {
        this.documentsOriginal = docs;
        this.documents = new ArrayList<>(documentsOriginal);
        this.sortMode = sortMode;
        onlySetFilter(searchQuery);
        Collections.sort(documents, getComparator());
        notifyDataSetChanged();
    }

    public void setData(List<VkDocument> docs) {
        this.documentsOriginal = docs;
        this.documents = new ArrayList<>(documentsOriginal);
        Collections.sort(documents, getComparator());
        notifyDataSetChanged();
    }

    public void setSortMode(SortMode sortMode) {
        this.sortMode = sortMode;
        Collections.sort(documents, getComparator());
        notifyDataSetChanged();
    }

    public void removeIndex(int position) {
        documents.remove(position);
        notifyItemRemoved(position);
    }
    public List<VkDocument> getData() {
        return documents;
    }

}
