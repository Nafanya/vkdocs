package io.github.nafanya.vkdocs.presentation.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.views.fragments.AudioPlayerFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.fragments.GifImageFragment;
import timber.log.Timber;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class DocumentsPagerAdapter extends FragmentStatePagerAdapter {
    private List<VkDocument> documents;
    private Fragment[] fragments;

    public DocumentsPagerAdapter(FragmentManager fm, List<VkDocument> docs) {
        super(fm);
        this.documents = docs;
        fragments = new Fragment[documents.size()];
    }


    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

        Timber.d("item pos = " + position);
        VkDocument document = documents.get(position);
        VkDocument.ExtType extType = documents.get(position).getExtType();
        Fragment ret = null;
            if (extType == VkDocument.ExtType.AUDIO) {
                ret = AudioPlayerFragment.newInstance(document);
            } else if (extType == VkDocument.ExtType.VIDEO) {

            } else if (extType == VkDocument.ExtType.IMAGE) {

            } else if (extType == VkDocument.ExtType.GIF) {
                ret = GifImageFragment.newInstance(document);
            } else {
                ret = new Fragment();
            }
        fragments[position] = ret;
        //ret = AudioPlayerFragment.newInstance(document);
        return ret;
    }

    public Fragment getFragment(int position) {
        return fragments[position];
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
