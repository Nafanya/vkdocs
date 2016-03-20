package io.github.nafanya.vkdocs.presentation.ui.views.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.utils.FileFormatter;
import timber.log.Timber;

public class BottomMenu extends BottomSheetDialog implements CompoundButton.OnCheckedChangeListener {

    @Bind(R.id.text_document_title)
    TextView title;

    @Bind(R.id.ic_document_type)
    ImageView documentTypeIcon;

    @Bind(R.id.switch1)
    Switch offlineSwitch;

    @Bind(R.id.bottom_download)
    RelativeLayout downloadButton;

    @Bind(R.id.bottom_rename)
    RelativeLayout renameButton;

    @Bind(R.id.bottom_delete)
    RelativeLayout deleteButton;

    private VkDocument doc;
    private int position;
    private FileFormatter fileFormatter;

    private MenuEventListener listener;

    public BottomMenu(@NonNull Context context, int position, VkDocument doc, FileFormatter fileFormatter, @NonNull MenuEventListener listener) {
        super(context);
        this.position = position;
        this.doc = doc;
        this.fileFormatter = fileFormatter;
        this.listener = listener;
        setContentView(R.layout.dialog_bottom);
        ButterKnife.bind(this);
        documentTypeIcon.setImageDrawable(fileFormatter.getIcon(doc, context));
        title.setText(doc.title);
        offlineSwitch.setChecked(doc.isOffline() || doc.isOfflineInProgress());
        offlineSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        listener.onClickMakeOffline(position, doc, isChecked);
    }

    @OnClick(R.id.bottom_download)
    public void onClickDownload(View v) {
        listener.onClickDownload(position, doc);
    }

    @OnClick(R.id.bottom_rename)
    public void onClickRename(View v) {
            listener.onClickRename(position, doc);
    }

    @OnClick(R.id.bottom_delete)
    public void onClickDelete(View v) {
        listener.onClickDelete(position, doc);
    }

    public interface MenuEventListener {
        void onClickMakeOffline(int position, VkDocument document, boolean isMakeOffline);
        void onClickRename(int position, VkDocument document);
        void onClickDelete(int position, VkDocument document);
        void onCloseContextMenu();
        void onClickDownload(int position, VkDocument document);
    }

    @Override
    protected void onStop() {
        super.onStop();
        listener.onCloseContextMenu();
    }
}
