package io.github.nafanya.vkdocs.presentation.ui.adapters;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.List;

import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.views.activities.DocumentViewerActivity;
import io.github.nafanya.vkdocs.presentation.ui.views.fragments.AudioPlayerFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.fragments.GifImageFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.fragments.ImageFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.fragments.UnknownTypeDocFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.fragments.VideoPlayerFragment;
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

        //throw new RuntimeException("lil");
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

        VkDocument document = documents.get(position);
        VkDocument.ExtType extType = documents.get(position).getExtType();
        Fragment ret = null;

        if (extType == VkDocument.ExtType.AUDIO) {
            ret = AudioPlayerFragment.newInstance(document);
        } else if (extType == VkDocument.ExtType.VIDEO) {
            ret = VideoPlayerFragment.newInstance(document, false);
        } else if (extType == VkDocument.ExtType.IMAGE) {
            ret = ImageFragment.newInstance(document);
        } else if (extType == VkDocument.ExtType.GIF) {
            ret = GifImageFragment.newInstance(document);
        } else {
            ret = UnknownTypeDocFragment.newInstance(document);
        }
        return ret;
    }

    public Fragment getFragment(int position) {
        return (Fragment)instantiateItem(null, position);
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
