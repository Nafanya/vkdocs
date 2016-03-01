package io.github.nafanya.vkdocs.presentation.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.utils.FileSizeFormatter;

public class MyDocsAdapter extends RecyclerView.Adapter<MyDocsAdapter.DocumentViewHolder> {
    private List<VKApiDocument> documents;
    private ItemEventListener listener;

    public MyDocsAdapter(ItemEventListener listener) {
        this.listener = listener;
    }

    @Override
    public DocumentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_document4, parent, false);
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

    public void setData(List<VKApiDocument> documents) {
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

        public void setup(VKApiDocument doc) {
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
    }
}
