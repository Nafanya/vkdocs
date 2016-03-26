package io.github.nafanya.vkdocs.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.net.impl.download.DownloadRequest;

public class FileFormatter {
    private String from;

    private Drawable musicBox;
    private Drawable movie;
    private Drawable filePdfBox;
    private Drawable image;
    private Drawable file;
    private Drawable archive;
    private Drawable gif;
    private Drawable text;

    public FileFormatter(Context context) {
        from = context.getString(R.string.from);
    }

    public Drawable getIcon(VkDocument doc, Context context) {
        if (musicBox == null) {
            musicBox = ContextCompat.getDrawable(context, R.drawable.music_box);
            movie = ContextCompat.getDrawable(context, R.drawable.movie);
            filePdfBox = ContextCompat.getDrawable(context, R.drawable.file_pdf_box);
            image = ContextCompat.getDrawable(context, R.drawable.image);
            file = ContextCompat.getDrawable(context, R.drawable.file);
            archive = ContextCompat.getDrawable(context, R.drawable.zip_box);
            gif = ContextCompat.getDrawable(context, R.drawable.image_vintage);
            text = ContextCompat.getDrawable(context, R.drawable.text_box);
        }

        if (doc.getExtType() == VkDocument.ExtType.AUDIO)
            return musicBox;

        if (doc.getExtType() == VkDocument.ExtType.VIDEO)
            return movie;

        if (doc.getExt().equals("pdf"))
            return filePdfBox;

        if (doc.getExtType() == VkDocument.ExtType.GIF)
            return gif;

        if (doc.getExtType() == VkDocument.ExtType.IMAGE)
            return image;

        if (doc.getExtType() == VkDocument.ExtType.ARCHIVE)
            return archive;

        if (doc.getExtType() == VkDocument.ExtType.TEXT)
            return text;

        return file;
    }

    public String formatFrom(long cur, long total) {
        String sz1 = formatSize(cur);
        String sz2 = formatSize(total);
        return sz1 + " " + from + " " + sz2;
    }

    public String formatFrom(DownloadRequest request) {
        return formatFrom(request.getBytes(), request.getTotalBytes());
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

    private DateFormat df = new SimpleDateFormat();
    public String formatDate(Date date) {
        return df.format(date);
    }
}
