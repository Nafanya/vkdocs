package io.github.nafanya.vkdocs.presentation.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.adapters.base.CommonItemEventListener;
import io.github.nafanya.vkdocs.utils.FileFormatter;
import timber.log.Timber;

public class MyDocsAdapter extends RecyclerView.Adapter<MyDocsAdapter.DocumentViewHolder> {
    private List<VkDocument> documents;
    private ItemEventListener listener;
    private FileFormatter fileFormatter;
    private Context context;

    public MyDocsAdapter(Context context, FileFormatter fileFormatter, ItemEventListener listener) {
        this.context = context;
        this.fileFormatter = fileFormatter;
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
        notifyItemChanged(pos);
    }

    public VKApiDocument getItem(int pos) {
        return documents.get(pos);
    }

    public class DocumentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.ic_document_type) ImageView documentTypeIcon;
        @Bind(R.id.ic_document_offline) ImageView documentOfflineIcon;
        @Bind(R.id.extraMenu) ImageButton contextMenu;
        @Bind(R.id.text_document_title) TextView title;
        @Bind(R.id.sortLabel) TextView sortLabel; // Only size yet
        @Bind(R.id.statusLabels) TextView statusLables; // Only size yet

        private ItemEventListener listener;

        public DocumentViewHolder(View view, ItemEventListener listener) {
            super(view);
            this.listener = listener;

            ButterKnife.bind(this, view);
            view.setOnClickListener(this);

            contextMenu.setOnClickListener(this);
        }

        public void setup(VkDocument doc) {
            contextMenu.setOnClickListener(this);

            title.setText(doc.title);
            /*
                TODO:
                1) Add type info to VKApiDocuent model since file type is more accurate when got through api.
                2) Add offline availability flag to model

                UPD: VKApiDocument ext field is always null, so we definitely need our own model.
             */

            documentTypeIcon.setImageDrawable(fileFormatter.getIcon(doc, context));

            if (doc.isOffline()) {
                documentOfflineIcon.setVisibility(View.VISIBLE);
            } else {
                documentOfflineIcon.setVisibility(View.GONE);
            }
            statusLables.setText(fileFormatter.formatSize(doc.size));
        }
        
        @Override
        public void onClick(View v) {
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

    public interface ItemEventListener extends CommonItemEventListener {
        void onClick(int position, VkDocument document);
        void onClickContextMenu(int position, VkDocument document);
    }
}
