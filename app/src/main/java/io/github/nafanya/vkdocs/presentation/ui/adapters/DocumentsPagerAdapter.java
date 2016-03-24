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
import io.github.nafanya.vkdocs.presentation.ui.views.fragments.VideoPlayerFragment;
import timber.log.Timber;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class DocumentsPagerAdapter extends FragmentStatePagerAdapter {
    private List<VkDocument> documents;
    private Fragment[] fragments;
    private int firstPosition;
    private FragmentManager fm;

    public DocumentsPagerAdapter(FragmentManager fm, List<VkDocument> docs, int firstPosition) {
        super(fm);
        this.fm = fm;
        this.documents = docs;
        this.firstPosition = firstPosition;
        fragments = new Fragment[documents.size()];
    }

    @Override
    public Fragment getItem(int position) {
        //throw new RuntimeException("lil");
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

        VkDocument document = documents.get(position);
        VkDocument.ExtType extType = documents.get(position).getExtType();
        Fragment ret = null;
        boolean itFirst = firstPosition == position;

        if (extType == VkDocument.ExtType.AUDIO) {
            ret = AudioPlayerFragment.newInstance(document, itFirst);
        } else if (extType == VkDocument.ExtType.VIDEO) {
            ret = VideoPlayerFragment.newInstance(document, itFirst);
        } else if (extType == VkDocument.ExtType.IMAGE) {
            ret = ImageFragment.newInstance(document, itFirst);
        } else if (extType == VkDocument.ExtType.GIF) {
            ret = GifImageFragment.newInstance(document, itFirst);
        } else {
            ret = new Fragment();
        }
        fragments[position] = ret;
        firstPosition = -1;
        return ret;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {//POSHLI NAHUY VSE DO ODNOY, I ADAPTER SVOI ZABERITE
        super.restoreState(state, loader);

        if (state != null) {
            Bundle bundle = (Bundle)state;
            bundle.setClassLoader(loader);

            Iterable<String> keys = bundle.keySet();
            for (String key: keys) {
                if (key.startsWith("f")) {
                    int index = Integer.parseInt(key.substring(1));
                    Fragment f = fm.getFragment(bundle, key);
                    if (f.getArguments() != null && firstPosition != index) {
                        f.getArguments().remove(AudioPlayerFragment.FIRST_KEY);
                        ((DocumentViewerActivity.OnPageChanged)f).onSetFirst(false);
                    } else if (f.getArguments() != null && firstPosition == index) {
                        f.getArguments().putBoolean(AudioPlayerFragment.FIRST_KEY, true);
                        ((DocumentViewerActivity.OnPageChanged)f).onSetFirst(true);
                    }
                    fragments[index] = f;
                }
            }
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        fragments[position] = null;
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
