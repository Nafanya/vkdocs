package io.github.nafanya.vkdocs.presentation.ui.fragments.viewer.base;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.View;

import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;

public class BaseViewerFragment extends Fragment implements OnPageChanged {

    private boolean isBecameVisible = false;
    private boolean isAlreadyNotifiedAboutVisible = false;

    public boolean isBecameVisible() {
        return isBecameVisible;
    }

    private static Snackbar snackbar;


    protected Drawable getPlaceholder(VkDocument doc) {
        Context context = getActivity();

        if (doc.getExt().equals("pdf"))
            return ContextCompat.getDrawable(context, R.drawable.pdf_placeholder);

        if (doc.getExtType().equals("doc") || doc.getExtType().equals("docx"))
            return ContextCompat.getDrawable(context, R.drawable.file_word_placeholder);

        if (doc.getExtType() == VkDocument.ExtType.ARCHIVE)
            return ContextCompat.getDrawable(context, R.drawable.zip_placeholder);

        if (doc.getExtType() == VkDocument.ExtType.TEXT)
            return ContextCompat.getDrawable(context, R.drawable.text_placeholder);

        return ContextCompat.getDrawable(context, R.drawable.file_unknown_type_placeholder);
    }

    public View rootForSnackbar() {
        return null;
    }

    public static void destroySnackbar() {
        hideSnackbar();
        snackbar = null;
    }

    public static void hideSnackbar() {
        if (snackbar != null)
            snackbar.dismiss();
    }

    public void showSnackBar(View.OnClickListener onClickListener) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(" ");
        builder.setSpan(new ImageSpan(getActivity(), R.drawable.open_in_new), 0, 1, ImageSpan.ALIGN_BASELINE);
        builder.append(" ");
        builder.append("Open in other app");

        if (snackbar == null) {
            View view = rootForSnackbar();
            snackbar = Snackbar
                    .make(view, builder, Snackbar.LENGTH_INDEFINITE);
            snackbar.setActionTextColor(Color.YELLOW);
        }

        snackbar.setAction("OPEN", onClickListener);
        if (!snackbar.isShown())
            snackbar.show();
    }

    public void showSnackbar(Snackbar snackbar) {
        destroySnackbar();
        snackbar.show();
    }

    @Override
    public void onResume() {//WARNING!!! super.onResume() at end of override function!!!
        super.onResume();
        if (isBecameVisible)
            onBecameVisible();
    }

    @Override
    public void onBecameVisible() {
        if (isAlreadyNotifiedAboutVisible)
            return;
        isAlreadyNotifiedAboutVisible = true;
        if (rootForSnackbar() == null && snackbar != null)
            snackbar.dismiss();
    }

    @Override
    public void onBecameInvisible() {
        isAlreadyNotifiedAboutVisible = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser != isBecameVisible) {
            isBecameVisible = isVisibleToUser;
            if (isBecameVisible) {
                if (isResumed())
                    onBecameVisible();
            } else
                onBecameInvisible();
        }
    }
}
