package io.github.nafanya.vkdocs.presentation.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.views.fragments.GifImageFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.fragments.ImageFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.fragments.MusicPlayFragment;
import timber.log.Timber;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class DocumentsPagerAdapter extends FragmentStatePagerAdapter {
    private List<VkDocument> documents;

    public DocumentsPagerAdapter(FragmentManager fm, List<VkDocument> docs) {
        super(fm);
        this.documents = docs;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        VkDocument document = documents.get(position);
        VkDocument.ExtType extType = documents.get(position).getExtType();
        Timber.d("[DocumentPagerAdapter] getItem: %d, docType: %s", position, extType);
        Fragment ret = null;
        if (extType == VkDocument.ExtType.AUDIO) {
            ret = MusicPlayFragment.newInstance(document);
        } else if (extType == VkDocument.ExtType.VIDEO) {

        } else if (extType == VkDocument.ExtType.IMAGE) {
            ret = ImageFragment.newInstance(document);
        } else if (extType == VkDocument.ExtType.GIF) {
            ret = GifImageFragment.newInstance(document);
        } else {
            ret = new Fragment();
        }
        return ret;
    }

    @Override
    public int getCount() {
        return documents.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return documents.get(position).title;
    }
}
