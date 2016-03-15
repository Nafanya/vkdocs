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
import io.github.nafanya.vkdocs.domain.download.base.DownloadManager;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.SortMode;
import io.github.nafanya.vkdocs.presentation.ui.adapters.base.AbstractAdapter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.base.CommonItemEventListener;
import io.github.nafanya.vkdocs.utils.DocumentComparator;
import io.github.nafanya.vkdocs.utils.FileFormatter;
import timber.log.Timber;

public class OfflineAdapter extends AbstractAdapter {
    private static final int DOCUMENT_STATE_NORMAL = 0;
    private static final int DOCUMENT_STATE_DOWNLOADING = 1;
    private FileFormatter fileFormatter;

    private List<VkDocument> documents;
    private ItemEventListener listener;
    private Context context;
    private SortMode sortMode;

    public OfflineAdapter(Context context, FileFormatter fileFormatter, SortMode sortMode, ItemEventListener listener) {
        this.context = context;
        this.fileFormatter = fileFormatter;
        this.listener = listener;
        this.sortMode = sortMode;
    }


    public void setData(List<VkDocument> documents) {
        this.documents = documents;
        Collections.sort(documents, DocumentComparator.getComparator(sortMode));
        notifyDataSetChanged();
    }

    public List<VkDocument> getData() {
        return documents;
    }

    public void setSortMode(SortMode sortMode) {
        this.sortMode = sortMode;
        Collections.sort(documents, DocumentComparator.getComparator(sortMode));
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

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public void removeIndex(int position) {
        documents.remove(position);
        notifyDataSetChanged();
    }

    public class DownloadingDocViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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

        private VkDocument prevDoc;

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
            title.setText(doc.title);
            if (prevDoc != null && prevDoc.getRequest() != null)
                prevDoc.getRequest().setObserver(null);
            prevDoc = doc;
            downloadProgress.setProgress(fileFormatter.getProgress(doc.getRequest()));
            size.setText(fileFormatter.formatFrom(doc.getRequest()));

            doc.getRequest().setObserver(new DownloadManager.RequestObserver() {
                @Override
                public void onProgress(int percentage) {
                    Timber.d("adapter on update: " + percentage + ", title = " + doc.title);
                    //.getString(R.string.from)
                    size.setText(fileFormatter.formatFrom(doc.getRequest()));
                    downloadProgress.setProgress(percentage);
                }

                @Override
                public void onComplete() {
                    doc.resetRequest();
                    notifyDataSetChanged();
                }

                @Override
                public void onError(Exception e) {
                    //TODO write here. snackbar?
                }

                @Override
                public void onInfiniteProgress() {
                    downloadProgress.setIndeterminate(true);
                }
            });
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (v == cancelButton)
                listener.onCancelDownloading(pos, documents.get(pos));
            else
                listener.onClick(pos, documents.get(pos));
        }
    }

    public class DocumentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.text_document_title)
        TextView title;

        @Bind(R.id.size)
        TextView size;

        private ItemEventListener listener;

        public DocumentViewHolder(View view, ItemEventListener listener) {
            super(view);
            this.listener = listener;

            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        public void setup(VkDocument doc) {
            title.setText(doc.title);
            size.setText(fileFormatter.formatSize(doc.size));
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            listener.onClick(pos, documents.get(pos));
        }
    }

    public interface ItemEventListener extends CommonItemEventListener {
        void onClick(int position, VkDocument document);
        void onCancelDownloading(int position, VkDocument document);
    }
}
