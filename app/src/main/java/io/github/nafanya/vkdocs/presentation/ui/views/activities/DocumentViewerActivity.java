package io.github.nafanya.vkdocs.presentation.ui.views.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.adapters.DocumentsPagerAdapter;
import io.github.nafanya.vkdocs.presentation.ui.views.fragments.AudioPlayerFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.fragments.base.OnPageChanged;
import timber.log.Timber;

public class DocumentViewerActivity extends AppCompatActivity implements AudioPlayerFragment.AudioPlayerControl {
    public static String POSITION_KEY = "position_key";
    public static String DOCUMENTS_KEY = "documents_key";
    //public static String NOT_FIRST_KEY = "not_first_key";

    private DocumentsPagerAdapter documentsPagerAdapter;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.container)
    ViewPager viewPager;


    private int position;
    private ArrayList<VkDocument> documents;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_document_viewer);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (state == null)
            state = getIntent().getExtras();
        position = state.getInt(POSITION_KEY);
        documents = state.getParcelableArrayList(DOCUMENTS_KEY);
        documentsPagerAdapter = new DocumentsPagerAdapter(getSupportFragmentManager(), documents);

        viewPager.addOnPageChangeListener(new OnPageChangeListener());
        viewPager.setAdapter(documentsPagerAdapter);
        setTitle(documentsPagerAdapter.getPageTitle(position));
        viewPager.setCurrentItem(position);
        Timber.d("position = " + position);
    }

    @Override
    public void onBackPressed() {
        ((OnPageChanged)documentsPagerAdapter.getFragment(position)).onBecameInvisible();
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(DOCUMENTS_KEY, documents);
        outState.putInt(POSITION_KEY, position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_document_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void nextAudio() {
        int i;
        int size = documents.size();
        for (i = (position + 1) % size;
             documents.get(i).getExtType() != VkDocument.ExtType.AUDIO && i != position;
             i = (i + 1) % size);
        viewPager.setCurrentItem(i, false);
    }

    @Override
    public void prevAudio() {
        int i;
        int size = documents.size();
        for (i = (position + size - 1) % size;
             documents.get(i).getExtType() != VkDocument.ExtType.AUDIO && i != position;
             i = (i + size - 1) % size);
        viewPager.setCurrentItem(i, false);
    }


    private class OnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            Timber.d("on page selected = " + position);
            setTitle(documentsPagerAdapter.getPageTitle(position));
            DocumentViewerActivity.this.position = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
