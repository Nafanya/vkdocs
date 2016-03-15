package io.github.nafanya.vkdocs.utils;

import java.util.Comparator;

import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.SortMode;

/**
 * Created by nafanya on 3/13/16.
 */
public class DocumentComparator {

    public static Comparator<VkDocument> offlineComparator(SortMode sortMode) {
        Comparator<VkDocument> comparator = getComparator(sortMode);
        return (lhs, rhs) -> {
            boolean eq = lhs.isOfflineInProgress() ^ lhs.isOfflineInProgress();
            if (!eq)
                return comparator.compare(lhs, rhs);
            if (lhs.isOfflineInProgress())
                return -1;
            return 1;
        };
    }

    private static Comparator<VkDocument> sizeComparator() {
        return (lhs, rhs) -> {
            long diff = rhs.size - lhs.size;
            if (diff < 0) {
                return -1;
            } else if (diff > 0) {
                return 1;
            }
            return 0;
        };
    }

    private static Comparator<VkDocument> dateComparator() {
        return (lhs, rhs) -> rhs.date.compareTo(lhs.date);
    }

    private static Comparator<VkDocument> nameComparator() {
        return (lhs, rhs) -> lhs.title.compareTo(rhs.title);
    }

    public static Comparator<VkDocument> getComparator(SortMode mode) {
        Comparator<VkDocument> cmp;
        switch (mode) {
            case DATE: cmp = dateComparator(); break;
            case NAME: cmp = nameComparator(); break;
            case SIZE: cmp = sizeComparator(); break;
            default:cmp = sizeComparator();
        }
        return cmp;
    }

}
