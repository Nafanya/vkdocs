package io.github.nafanya.vkdocs.presentation.ui.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.download.base.DownloadRequest;
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

    public OfflineAdapter(Context context, FileFormatter fileFormatter, SortMode sortMode, ItemEventListener listener) {
        super(context, fileFormatter, sortMode);
        this.listener = listener;
    }

    public void setData(List<VkDocument> documents) {
        this.documents = documents;
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

        if (viewType == DOCUMENT_STATE_NORMAL) {
            View view = inflater.inflate(R.layout.item_document, parent, false);
            return new DocumentViewHolder(view, listener);
        } else {
            View view = inflater.inflate(R.layout.item_document_downloading, parent, false);
            return new DownloadingDocViewHolder(view, listener);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DownloadingDocViewHolder)
            ((DownloadingDocViewHolder)holder).setup(documents.get(position));
        else
            ((DocumentViewHolder)holder).setup(documents.get(position));

    }

    public void removeIndex(int position) {
        documents.remove(position);
        notifyDataSetChanged();
    }

    public class DownloadingDocViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Nullable
        @Bind(R.id.ic_document_type)
        ImageView documentTypeIcon;

        @Nullable
        @Bind(R.id.text_document_title)
        TextView title;

        @Nullable
        @Bind(R.id.size)
        TextView size;

        @Nullable
        @Bind(R.id.down_progress)
        ProgressBar downloadProgress;

        @Nullable
        @Bind(R.id.down_button)
        ImageView cancelButton;

        @Nullable
        @Bind(R.id.context_menu)
        ImageView contextMenuButton;

        private Subscription prevSubscription = Subscriptions.empty();

        private ItemEventListener listener;

        public DownloadingDocViewHolder(View view, ItemEventListener listener) {
            super(view);
            this.listener = listener;

            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
            cancelButton.setOnClickListener(this);
        }

        //TODO maybe add downloaded bytes and full size in progress callbacks
        //TODO remove indefinite progress, we always know size of file from VkApiDocument. pass it in download manager?
        public void setup(VkDocument doc) {
            //documentTypeIcon.setImageDrawable(fileFormatter.getIcon(doc, context));

            title.setText(doc.title);
            prevSubscription.unsubscribe();
            downloadProgress.setProgress(fileFormatter.getProgress(doc.getRequest()));
            size.setText(fileFormatter.formatFrom(doc.getRequest()));

            prevSubscription = doc.getRequest().addListener(new DownloadRequest.RequestListener() {
                @Override
                public void onProgress(int percentage) {
                    Timber.d("adapter on update: " + percentage + ", title = " + doc.title);
                    //.getString(R.string.from)
                    size.setText(fileFormatter.formatFrom(doc.getRequest()));
                    downloadProgress.setProgress(percentage);
                }

                @Override
                public void onComplete() {
                    doc.setPath(doc.getRequest().getDest());
                    Timber.d("path doc = " + doc.getPath());
                    doc.resetRequest();
                    listener.onCompleteDownloading(getAdapterPosition(), doc);
                    Collections.sort(documents, DocumentComparator.offlineComparator(sortMode));
                    notifyDataSetChanged();
                }

                @Override
                public void onError(Exception e) {
                    //TODO write here. snackbar?
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (listener == null)
                return;
            int pos = getAdapterPosition();
            if (v == cancelButton)
                listener.onCancelDownloading(pos, documents.get(pos));
            else
                listener.onClick(pos, documents.get(pos));
        }
    }

    public interface ItemEventListener extends CommonItemEventListener {
        void onCancelDownloading(int position, VkDocument document);
        void onCompleteDownloading(int position, VkDocument document);
    }
}
