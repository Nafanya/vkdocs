package io.github.nafanya.vkdocs.presentation.ui.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.net.impl.download.DownloadRequest;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.SortMode;
import io.github.nafanya.vkdocs.presentation.ui.adapters.base.BaseSortedAdapter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.base.CommonItemEventListener;
import io.github.nafanya.vkdocs.utils.DocumentComparator;
import io.github.nafanya.vkdocs.utils.FileFormatter;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class OfflineAdapter extends BaseSortedAdapter {
    private static final int DOCUMENT_STATE_NORMAL = 0;
    private static final int DOCUMENT_STATE_DOWNLOADING = 1;
    private ItemEventListener listener;
    private DownloadRequest[] requests;
    private DownloadRequest.RequestListener[] listeners;

    private RecyclerView.AdapterDataObserver dataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            if (listeners != null) {
                for (int i = 0; i < requests.length; ++i)
                    if (requests[i] != null)
                        requests[i].removeListener(listeners[i]);
            }

            listeners = new DownloadRequest.RequestListener[documents.size()];
            requests = new DownloadRequest[documents.size()];
            for (int i = 0; i < documents.size(); ++i)
                requests[i] = documents.get(i).getRequest();
        }
    };

    public OfflineAdapter(Context context, FileFormatter fileFormatter, SortMode sortMode, ItemEventListener listener) {
        super(context, fileFormatter, sortMode);
        this.listener = listener;
        registerAdapterDataObserver(dataObserver);
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
        if (documents.get(position).isOfflineInProgress())
            return DOCUMENT_STATE_DOWNLOADING;
        return DOCUMENT_STATE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
            listeners[position] = ((DownloadingDocViewHolder) holder).setup(documents.get(position));
        } else {
            ((DocumentViewHolder) holder).setup(documents.get(position));
        }
    }

    public class DownloadingDocViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Nullable
        @Bind(R.id.ic_document_type)
        ImageView documentTypeIcon;

        @Nullable
        @Bind(R.id.ic_document_offline)
        ImageView documentOfflineIcon;

        @Nullable
        @Bind(R.id.ic_document_offline_progress)
        ImageView documentOfflineInProgressIcon;

        @Nullable
        @Bind(R.id.text_document_title)
        TextView title;
        @Nullable
        @Bind(R.id.statusLabels)
        TextView size;
        @Bind(R.id.sortLabel)
        TextView sortLabel;
        @Nullable
        @Bind(R.id.progress)
        ProgressBar downloadProgress;
        @Nullable
        @Bind(R.id.buttonContextMenu)
        ImageButton buttonContext;
        @Nullable
        @Bind(R.id.buttonCancel)
        ImageButton buttonCancel;

        private DownloadRequest.RequestListener prevListener;
        private ItemEventListener listener;

        public DownloadingDocViewHolder(View view, ItemEventListener listener) {
            super(view);
            this.listener = listener;

            ButterKnife.bind(this, view);

            documentOfflineIcon.setVisibility(View.GONE);
            documentOfflineInProgressIcon.setVisibility(View.GONE);

            downloadProgress.setVisibility(View.VISIBLE);
            sortLabel.setVisibility(View.GONE);

            view.setOnClickListener(this);
            buttonContext.setOnClickListener(this);
            buttonCancel.setOnClickListener(this);
        }

        private class ProgressListener implements DownloadRequest.RequestListener {
            private VkDocument doc;
            public ProgressListener(VkDocument doc) {
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
                Timber.d("doc = " + doc + ", request = " + doc.getRequest());
                doc.setPath(doc.getRequest().getDest());
                Timber.d("path doc = " + doc.getPath());
                doc.resetRequest();
                listener.onCompleteDownloading(getAdapterPosition(), doc);
                //TODO fix it
                //Collections.sort(documents, DocumentComparator.offlineComparator(sortMode));
                //notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
            }
        }

        //TODO maybe add downloaded bytes and full size in progress callbacks
        //TODO remove indefinite progress, we always know size of file from VkApiDocument. pass it in download manager?
        public DownloadRequest.RequestListener setup(VkDocument doc) {
            Timber.d("setup = " + doc.title + ", request = " + doc.getRequest());
            documentTypeIcon.setImageDrawable(fileFormatter.getIcon(doc, context));
            title.setText(doc.title);
            if (prevListener != null)
                doc.getRequest().removeListener(prevListener);

            Timber.d("LIST SIZE0 = " + doc.getRequest().listeners.size());
            downloadProgress.setProgress(fileFormatter.getProgress(doc.getRequest()));
            size.setText(fileFormatter.formatFrom(doc.getRequest()));
            Timber.d("LIST SIZE1 = " + doc.getRequest().listeners.size());
            if (doc.isDownloading()) {
                buttonContext.setVisibility(View.GONE);
                buttonCancel.setVisibility(View.VISIBLE);
            } else {
                buttonContext.setVisibility(View.VISIBLE);
                buttonCancel.setVisibility(View.GONE);
            }
            Timber.d("LIST SIZE2 = " + doc.getRequest().listeners.size());
            Timber.d("request prev = " + doc.getRequest());
            prevListener = new ProgressListener(doc);
            doc.getRequest().addListener(prevListener);
            return prevListener;
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
