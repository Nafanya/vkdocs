package io.github.nafanya.vkdocs.presentation.ui.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
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

    private VkDocument doc;
    private FileFormatter fileFormatter;

    private MenuEventListener listener;

    public BottomMenu(@NonNull Context context, VkDocument doc, FileFormatter fileFormatter, MenuEventListener listener) {
        super(context);
        this.doc = doc;
        this.fileFormatter = fileFormatter;
        this.listener = listener;
        setContentView(R.layout.dialog_bottom);
        ButterKnife.bind(this);
        documentTypeIcon.setImageDrawable(fileFormatter.getIcon(doc));
        title.setText(doc.title);
        offlineSwitch.setOnCheckedChangeListener(this);
        offlineSwitch.setChecked(doc.isOffline());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        listener.onClickMakeOffline(doc, isChecked);
    }

    public interface MenuEventListener {
        void onClickMakeOffline(VkDocument document, boolean isMakeOffline);
    }
}
