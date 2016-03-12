package io.github.nafanya.vkdocs.utils;

import android.app.Application;

import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.download.base.DownloadRequest;

public class FileFormatter {
    private String from;

    public FileFormatter(Application app) {
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
