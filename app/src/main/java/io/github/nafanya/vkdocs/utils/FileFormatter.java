package io.github.nafanya.vkdocs.utils;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.download.base.DownloadRequest;
import io.github.nafanya.vkdocs.domain.model.VkDocument;

public class FileFormatter {
    private String from;

    private Drawable musicBox;
    private Drawable movie;
    private Drawable filePdfBox;
    private Drawable image;
    private Drawable file;

    public FileFormatter(Context context) {
        from = context.getString(R.string.from);

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

    public String formatFrom(DownloadRequest request) {
        String sz1 = formatSize(request.getBytes());
        String sz2 = formatSize(request.getTotalBytes());
        return sz1 + " " + from + " " + sz2;
    }

    public String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + "B";
        } else if (bytes < 1024 * 1024) {
            return getFormatSize(bytes, 1024, "KB");
        } else if (bytes < 1024 * 1024 * 1024) {
            return getFormatSize(bytes, 1024 * 1024, "MB");
        } else {
            return getFormatSize(bytes, 1024 * 1024 * 1024, "G");
        }
    }

    public int getProgress(DownloadRequest request) {
        return (int) (request.getBytes() * 1.0 / request.getTotalBytes() * 100);
    }

    private String getFormatSize(long size, int div, String metr) {
        long kb = size / div;
        if (kb < 10)
            return (int)(size * 1.0 / div * 10) / 10.0 + metr;
        return size / div + metr;
    }
}
