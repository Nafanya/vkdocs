package io.github.nafanya.vkdocs.presentation.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.utils.FileFormatUtils;
import timber.log.Timber;

public class MyDocsAdapter extends RecyclerView.Adapter<MyDocsAdapter.DocumentViewHolder> {
    private List<VkDocument> documents;
    private ItemEventListener listener;
    private FileFormatUtils fileFormatter;

    public MyDocsAdapter(FileFormatUtils fileFormatter, ItemEventListener listener) {
        this.fileFormatter = fileFormatter;
        this.listener = listener;
    }

    @Override
    public DocumentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_document4_with_make_offline_button, parent, false);
        return new DocumentViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(DocumentViewHolder holder, int position) {
        holder.setup(documents.get(position));
    }

    @Override
    public long getItemId(int position) {
        return documents.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public void setData(List<VkDocument> documents) {
        this.documents = documents;
        notifyDataSetChanged();
    }

    public void removeIndex(int pos) {
        documents.remove(pos);
        notifyDataSetChanged();
    }

    public VKApiDocument getItem(int pos) {
        return documents.get(pos);
    }

    public class DocumentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.icon_offline_label)
        ImageView iconOffline;

        @Bind(R.id.text_doctitle)
        TextView title;

        @Bind(R.id.size)
        TextView size;

        @Bind(R.id.context_menu)
        ImageView contextMenuButton;

        @Bind(R.id.make_offline)
        ImageView makeOfflineButton;

        private ItemEventListener listener;

        public DocumentViewHolder(View view, ItemEventListener listener) {
            super(view);
            this.listener = listener;

            ButterKnife.bind(this, view);
            view.setOnClickListener(this);

            contextMenuButton.setOnClickListener(this);
            makeOfflineButton.setOnClickListener(this);
        }

        public void setup(VkDocument doc) {
            title.setText(doc.title);
            size.setText(fileFormatter.formatSize(doc.size));

            if (doc.isOffline() || doc.isOfflineInProgress())
                makeOfflineButton.setVisibility(View.GONE);
            else
                makeOfflineButton.setVisibility(View.VISIBLE);

            if (doc.isNotOffline())
                iconOffline.setVisibility(View.GONE);
            else
                iconOffline.setVisibility(View.VISIBLE);
        }
        
        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (v == itemView)
                listener.onClick(pos, documents.get(pos));
            else if (v == contextMenuButton) {
                //TODO add context menu
            } else if (v == makeOfflineButton) {
                listener.onClickMakeOffline(pos, documents.get(pos));
                Timber.d("ON CLICK MAKE OFFLINE");
            }
        }
    }

    public interface ItemEventListener {
        void onClick(int position, VKApiDocument document);
        //onClickContextMenu()
        void onClickMakeOffline(int position, VkDocument document);
    }
}
