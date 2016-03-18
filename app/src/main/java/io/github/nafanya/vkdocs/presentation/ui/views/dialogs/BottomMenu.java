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
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.utils.FileFormatter;
import timber.log.Timber;

public class BottomMenu extends BottomSheetDialog implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    @Bind(R.id.text_document_title)
    TextView title;

    @Bind(R.id.ic_document_type)
    ImageView documentTypeIcon;

    @Bind(R.id.switch1)
    Switch offlineSwitch;

    @Bind(R.id.bottom_rename)
    RelativeLayout renameButton;

    private VkDocument doc;
    private int position;
    private FileFormatter fileFormatter;

    private MenuEventListener listener;

    public BottomMenu(@NonNull Context context, int position, VkDocument doc, FileFormatter fileFormatter, MenuEventListener listener) {
        super(context);
        this.position = position;
        this.doc = doc;
        this.fileFormatter = fileFormatter;
        this.listener = listener;
        setContentView(R.layout.dialog_bottom);
        ButterKnife.bind(this);
        documentTypeIcon.setImageDrawable(fileFormatter.getIcon(doc, context));
        title.setText(doc.title);
        offlineSwitch.setOnCheckedChangeListener(this);
        offlineSwitch.setChecked(doc.isOffline());
        renameButton.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        listener.onClickMakeOffline(doc, isChecked);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bottom_rename)
            listener.onClickRename(doc);
    }

    public interface MenuEventListener {
        void onClickMakeOffline(VkDocument document, boolean isMakeOffline);
        void onClickRename(VkDocument document);
    }
}
