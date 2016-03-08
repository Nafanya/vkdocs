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

import com.vk.sdk.api.model.VKApiDocument;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.download.base.DownloadManager;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.utils.FileSizeFormatter;
import timber.log.Timber;

public class OfflineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int DOCUMENT_STATE_NORMAL = 0;
    private static final int DOCUMENT_STATE_DOWNLOADING = 1;

    private List<VkDocument> documents;
    private ItemEventListener listener;

    public OfflineAdapter(ItemEventListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (documents.get(position).getRequest() == null)
            return DOCUMENT_STATE_NORMAL;
        return DOCUMENT_STATE_DOWNLOADING;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == DOCUMENT_STATE_NORMAL) {
            View view = inflater.inflate(R.layout.item_document4, parent, false);
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

    public void setData(List<VkDocument> documents) {
        this.documents = documents;
        notifyDataSetChanged();
    }

    public class DownloadingDocViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Nullable
        @Bind(R.id.text_doctitle)
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

        //TODO extract string resource. pass activity in the adapter for getting it? i don't know
        //TODO maybe add downloaded bytes and full size in progress callbacks
        //TODO remove indefinite progress, we always know size of file from VkApiDocument. pass it in download manager?
        public void setup(VkDocument doc) {
            title.setText(doc.title);
            long sizeBytes = doc.size;
            String sz = FileSizeFormatter.format(sizeBytes);
            if (prevDoc != null && prevDoc.getRequest() != null)
                prevDoc.getRequest().setObserver(null);
            prevDoc = doc;
            downloadProgress.setProgress((int)(doc.getRequest().getBytes() * 1.0 / doc.size * 100));
            size.setText(FileSizeFormatter.format(doc.getRequest().getBytes()) + " from " + sz);

            doc.getRequest().setObserver(new DownloadManager.RequestObserver() {
                @Override
                public void onProgress(int percentage) {
                    size.setText(FileSizeFormatter.format(sizeBytes * percentage / 100) + " from " + sz);
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
        @Bind(R.id.text_doctitle)
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
            size.setText(FileSizeFormatter.format(doc.size));
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            listener.onClick(pos, documents.get(pos));
        }
    }

    public interface ItemEventListener {
        void onClick(int position, VKApiDocument document);
        void onCancelDownloading(int position, VkDocument document);
    }
}
