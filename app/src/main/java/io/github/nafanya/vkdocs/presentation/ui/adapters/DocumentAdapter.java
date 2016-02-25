package io.github.nafanya.vkdocs.presentation.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.List;

import io.github.nafanya.vkdocs.R;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {
    private List<VKApiDocument> documents;
    private DocumentViewHolder.DocumentClickListener listener;

    public DocumentAdapter(DocumentViewHolder.DocumentClickListener listener) {
        this.listener = listener;
    }

    @Override
    public DocumentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.document_view, parent, false);
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

    public static class DocumentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public interface DocumentClickListener {
            void onClickDelete(int position);
        }

        private DocumentClickListener listener;

        public DocumentViewHolder(View view, DocumentClickListener listener) {
            super(view);
            this.listener = listener;

            view.setTag(view.findViewById(R.id.title));
            view.findViewById(R.id.delete).setOnClickListener(this);
        }

        public void setup(VKApiDocument doc) {
            ((TextView)itemView.getTag()).setText(doc.title);
        }


        @Override
        public void onClick(View v) {
            listener.onClickDelete(getAdapterPosition());
        }
    }
}
