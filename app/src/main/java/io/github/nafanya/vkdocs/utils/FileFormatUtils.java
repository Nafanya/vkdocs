package io.github.nafanya.vkdocs.utils;

import android.app.Application;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.download.DownloadRequest;

public class FileFormatUtils {
    private String from;
    private SimpleDateFormat dateFormat;

    public FileFormatUtils(Application app) {
        from = app.getString(R.string.from);
        dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());
    }

    public String formatFrom(DownloadRequest request) {
        String sz1 = formatSize(request.getBytes());
        String sz2 = formatSize(request.getTotalBytes());
        return sz1 + " " + from + " " + sz2;
    }

    public String formatDate(Date date) {
        return dateFormat.format(date);
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

    private String getFormatSize(long size, int div, String metr) {
        long kb = size / div;
        if (kb < 10)
            return (int)(size * 1.0 / div * 10) / 10.0 + metr;
        return size / div + metr;
    }

}
