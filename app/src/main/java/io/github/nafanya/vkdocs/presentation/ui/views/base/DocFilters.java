package io.github.nafanya.vkdocs.presentation.ui.views.base;

import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;

import static io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter.*;

//TODO write extension here
public class DocFilters {

    public static DocFilter ALL =
            new DocumentsPresenter.SimpleDocFilter("*");

    public static DocFilter TEXT = new SimpleDocFilter("doc", "docx", "xls", "xlsx", "ppt", "pptx", "rtx", "pdf", "txt", "djvu", "ps");

    public static DocFilter BOOKS = new SimpleDocFilter("fb2");

    public static DocFilter ARCHIVES = new SimpleDocFilter("rar", "zip", "bz2");

    public static DocFilter GIFS = new SimpleDocFilter("gif");

    public static DocFilter IMAGES = new SimpleDocFilter("png", "jpg");

    public static DocFilter MUSIC = new SimpleDocFilter("mp3", "wav", "flac", "wave");

    public static DocFilter VIDEO = new SimpleDocFilter("mp4", "3gp", "avi");

    public static DocFilter OTHER = negate(TEXT, BOOKS, ARCHIVES, GIFS, IMAGES, MUSIC, VIDEO);

    public static DocFilter negate(final DocFilter... filters) {
        return doc -> {
            for (DocFilter f : filters)
                if (f.filter(doc))
                    return false;
            return true;
        };
    }
}
