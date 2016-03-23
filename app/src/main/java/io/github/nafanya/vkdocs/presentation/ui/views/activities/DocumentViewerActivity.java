package io.github.nafanya.vkdocs.presentation.ui.views.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import timber.log.Timber;

public class DocumentViewerActivity extends AppCompatActivity {
    public static String POSITION_KEY = "position_key";
    public static String DOCUMENTS_KEY = "documents_key";

    public interface OnPageChanged {
        void onCurrent();
        void onNotCurrent();
    }
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
        viewPager.setCurrentItem(position);
        setTitle(documentsPagerAdapter.getPageTitle(position));

        //Fragment fragment = documentsPagerAdapter.getFragment(position);
        //((OnPageChanged)fragment).onCurrent();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(DOCUMENTS_KEY, documents);
        outState.putInt(POSITION_KEY, viewPager.getCurrentItem());
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


    private class OnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            Timber.d("on page selected = " + position);
            setTitle(documentsPagerAdapter.getPageTitle(position));
            int prevPosition = DocumentViewerActivity.this.position;
            Fragment fragment = documentsPagerAdapter.getFragment(prevPosition);
            if (fragment != null)
                ((OnPageChanged)fragment).onNotCurrent();
            DocumentViewerActivity.this.position = position;
            fragment = documentsPagerAdapter.getFragment(position);
            ((OnPageChanged)fragment).onCurrent();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
