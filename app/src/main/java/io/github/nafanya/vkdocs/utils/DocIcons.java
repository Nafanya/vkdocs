package io.github.nafanya.vkdocs.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;

public class DocIcons {
    private Drawable musicBox;
    private Drawable movie;
    private Drawable filePdfBox;
    private Drawable image;
    private Drawable file;

    public DocIcons(Context context) {
        musicBox = ContextCompat.getDrawable(context, R.drawable.music_box);
        movie = ContextCompat.getDrawable(context, R.drawable.movie);
        filePdfBox = ContextCompat.getDrawable(context, R.drawable.file_pdf_box);
        image = ContextCompat.getDrawable(context, R.drawable.image);
        file = ContextCompat.getDrawable(context, R.drawable.file);
    }

    public Drawable getIcon(VkDocument doc) {
        if (doc.getExtType() == VkDocument.ExtType.AUDIO)
            return musicBox;

        if (doc.getExtType() == VkDocument.ExtType.VIDEO)
            return movie;

        if (doc.getExt().equals("pdf"))
            return filePdfBox;

        if (doc.getExtType() == VkDocument.ExtType.IMAGE)
            return image;
        return file;
    }
}
