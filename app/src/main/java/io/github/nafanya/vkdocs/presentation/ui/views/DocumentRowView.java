package io.github.nafanya.vkdocs.presentation.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.utils.FileFormatter;

/**
 * Created by nafanya on 3/25/16.
 */
public class DocumentRowView extends RelativeLayout {

    @Bind(R.id.ic_document_type) ImageView documentTypeIcon;
    @Bind(R.id.ic_document_offline) ImageView documentOfflineIcon;
    @Bind(R.id.ic_document_cache_offline_progress) ImageView documentOfflineInProgressIcon;

    @Bind(R.id.ic_document_cached) ImageView documentCachedIcon;
    @Bind(R.id.ic_document_cache_progress) ImageView documentCacheInProgressIcon;

    @Bind(R.id.buttonContextMenu) ImageButton contextMenu;
    @Bind(R.id.text_document_title) TextView title;
    @Bind(R.id.sortLabel) TextView sortLabel;
    @Bind(R.id.statusLabels) TextView statusLables;

    private FileFormatter formatter;
    private Context context;
    private boolean downloading;

    public DocumentRowView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_document2, this, true);

        ButterKnife.bind(this);

        formatter = new FileFormatter(context);
        downloading = false;
    }

    public void setup(VkDocument document) {
        title.setText(document.title);
        documentTypeIcon.setImageDrawable(formatter.getIcon(document, context));

        documentOfflineIcon.setVisibility(View.GONE);
        documentOfflineInProgressIcon.setVisibility(View.GONE);
        documentCachedIcon.setVisibility(View.GONE);
        documentCacheInProgressIcon.setVisibility(View.GONE);

        if (document.isOffline()) {
            documentOfflineIcon.setVisibility(View.VISIBLE);
        } else if (document.isOfflineInProgress()) {
            documentOfflineInProgressIcon.setVisibility(View.VISIBLE);
        } else if (document.isCached()) {
            documentCachedIcon.setVisibility(View.VISIBLE);
        } else if (document.isCacheInProgress()) {
            documentCacheInProgressIcon.setVisibility(View.VISIBLE);
        }

        /*final int sortLabelText;
        final String statusLabelText;
        switch (sortMode) {
            case DATE:
                sortLabelText = R.string.label_modified;
                statusLabelText = fileFormatter.formatDate(document.date);
                break;
            case SIZE:case NAME:
                sortLabelText = R.string.label_size;
                statusLabelText = fileFormatter.formatSize(document.size);
                break;
            default:
                sortLabelText = R.string.label_size;
                statusLabelText = fileFormatter.formatSize(document.size);
        }

        sortLabel.setText(sortLabelText);
        statusLables.setText(statusLabelText);
        */
    }



}
