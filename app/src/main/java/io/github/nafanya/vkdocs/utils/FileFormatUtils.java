package io.github.nafanya.vkdocs.utils;

import android.app.Application;
import android.content.Context;

import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.download.DownloadRequest;

public class FileFormatUtils {
    private String from;

    public FileFormatUtils(Application app) {
        from = app.getString(R.string.from);
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

    private String getFormatSize(long size, int div, String metr) {
        long kb = size / div;
        if (kb < 10)
            return (int)(size * 1.0 / div * 10) / 10.0 + metr;
        return size / div + metr;
    }

}
