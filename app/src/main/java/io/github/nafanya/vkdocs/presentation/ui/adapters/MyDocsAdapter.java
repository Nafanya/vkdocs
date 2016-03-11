package io.github.nafanya.vkdocs.presentation.ui.adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import io.github.nafanya.vkdocs.utils.FileFormatUtils;

public class MyDocsAdapter extends RecyclerView.Adapter<MyDocsAdapter.DocumentViewHolder> {
    private List<VkDocument> documents;
    private ItemEventListener listener;
    private FileFormatUtils fileFormatter;
    private Context context;

    public MyDocsAdapter(Context context, FileFormatUtils fileFormatter, ItemEventListener listener) {
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
        }

        public void setup(VkDocument doc) {
            title.setText(doc.title);
            /*
                TODO:
                1) Add type info to VKApiDocuent model since file type is more accurate when got through api.
                2) Add offline availability flag to model

                UPD: VKApiDocument ext field is always null, so we definitely need our own model.
             */

            if (doc.getExtType() == VkDocument.ExtType.AUDIO)
                documentTypeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.music_box));
            else if (doc.getExtType() == VkDocument.ExtType.VIDEO)
                documentTypeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.movie));
            else if (doc.getExt().equals("pdf"))
                documentTypeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.file_pdf_box));
            else if (doc.getExtType() == VkDocument.ExtType.IMAGE)
                documentTypeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.image));
            else
                documentTypeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.file));

            if (doc.getId() % 5 == 1) {
                documentOfflineIcon.setVisibility(View.GONE);
            } else {
                documentOfflineIcon.setVisibility(View.VISIBLE);
            }
            statusLables.setText(fileFormatter.formatSize(doc.size));
        }
        
        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (v == itemView)
                listener.onClick(pos, documents.get(pos));
            else if (v == contextMenu) {
                Snackbar.make(v, "Context menu for item #" + pos, Snackbar.LENGTH_SHORT).show();
            }
//            } else if (v == makeOfflineButton) {
//                listener.onClickMakeOffline(pos, documents.get(pos));
//                Timber.d("ON CLICK MAKE OFFLINE");
//            }
        }
    }

    public interface ItemEventListener extends CommonItemEventListener {
        void onClick(int position, VkDocument document);
        //onClickContextMenu()
        void onClickMakeOffline(int position, VkDocument document);
    }
}
