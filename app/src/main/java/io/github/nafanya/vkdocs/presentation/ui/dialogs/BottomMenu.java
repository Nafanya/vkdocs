package io.github.nafanya.vkdocs.presentation.ui.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.utils.FileFormatter;

public class BottomMenu extends BottomSheetDialog implements CompoundButton.OnCheckedChangeListener {

    @Bind(R.id.text_document_title)
    TextView title;

    @Bind(R.id.ic_document_type)
    ImageView documentTypeIcon;

    @Bind(R.id.body_available_offline)
    RelativeLayout offlineLayout;

    @Bind(R.id.switch1)
    SwitchCompat offlineSwitch;

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

        setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = (FrameLayout) d.findViewById(android.support.design.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        listener.onClickMakeOffline(position, doc, isChecked);
    }

    @OnClick(R.id.body_available_offline)
    public void onClickOffline(View v) {
        offlineSwitch.setChecked(!offlineSwitch.isChecked());
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

    @OnClick(R.id.bottom_share)
    public void onClickShare(View v) { listener.onClickShare(doc); }

    @OnClick(R.id.bottom_share_external)
    public void onClickShareExternal(View v) { listener.onClickShareExternal(doc); }

    public interface MenuEventListener {
        void onClickMakeOffline(int position, VkDocument document, boolean isMakeOffline);
        void onClickRename(int position, VkDocument document);
        void onClickDelete(int position, VkDocument document);
        void onCloseContextMenu();
        void onClickDownload(int position, VkDocument document);
        void onClickShare(VkDocument document);
        void onClickShareExternal(VkDocument document);
    }

    @Override
    protected void onStop() {
        super.onStop();
        listener.onCloseContextMenu();
    }
}
