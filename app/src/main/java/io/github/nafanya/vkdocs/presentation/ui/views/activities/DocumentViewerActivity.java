package io.github.nafanya.vkdocs.presentation.ui.views.activities;

import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.util.List;

import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.views.fragments.MusicPlayFragment;

public class DocumentViewerActivity extends AppCompatActivity {

    public static String DOCUMENTS_KEY = "documents_key";

    private DocumentsPagerAdapter documentsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_document_viewer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        List<VkDocument> documents = state.getParcelableArrayList(DOCUMENTS_KEY);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        documentsPagerAdapter = new DocumentsPagerAdapter(getSupportFragmentManager(), documents);

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(documentsPagerAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_document_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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
            VkDocument.ExtType extType = documents.get(position).getExtType();
            Fragment ret = null;
            if (extType == VkDocument.ExtType.AUDIO) {
                ret = MusicPlayFragment.newInstance();
            } else if (extType == VkDocument.ExtType.VIDEO) {

            } else if (extType == VkDocument.ExtType.IMAGE) {

            } else if (extType == VkDocument.ExtType.GIF) {

            } else {

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
}
