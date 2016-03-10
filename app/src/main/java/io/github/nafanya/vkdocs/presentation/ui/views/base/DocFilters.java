package io.github.nafanya.vkdocs.presentation.ui.views.base;

import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;

import static io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter.*;

//TODO write extension here
public class DocFilters {

    public static DocFilter ALL =
            new DocumentsPresenter.SimpleDocFilter(
                    VkDocument.ExtType.TEXT,
                    VkDocument.ExtType.BOOK,
                    VkDocument.ExtType.ARCHIVE,
                    VkDocument.ExtType.GIF,
                    VkDocument.ExtType.IMAGE,
                    VkDocument.ExtType.AUDIO,
                    VkDocument.ExtType.VIDEO,
                    VkDocument.ExtType.UNKNOWN);

    public static DocFilter TEXT = new SimpleDocFilter(VkDocument.ExtType.TEXT);

    public static DocFilter BOOKS = new SimpleDocFilter(VkDocument.ExtType.BOOK);

    public static DocFilter ARCHIVES = new SimpleDocFilter(VkDocument.ExtType.ARCHIVE);

    public static DocFilter GIFS = new SimpleDocFilter(VkDocument.ExtType.GIF);

    public static DocFilter IMAGES = new SimpleDocFilter(VkDocument.ExtType.IMAGE);

    public static DocFilter MUSIC = new SimpleDocFilter(VkDocument.ExtType.AUDIO);

    public static DocFilter VIDEO = new SimpleDocFilter(VkDocument.ExtType.VIDEO);

    public static DocFilter OTHER = new SimpleDocFilter(VkDocument.ExtType.UNKNOWN);
}
