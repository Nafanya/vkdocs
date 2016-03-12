package io.github.nafanya.vkdocs.presentation.ui.views;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.presenter.base.filter.ExtDocFilter;
import timber.log.Timber;

public class DocumentsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbar_spinner) Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_documents);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getSupportActionBar().getThemedContext(),
                R.array.doc_type_filter_items, R.layout.appbar_filter_title);
        adapter.setDropDownViewResource(R.layout.appbar_filter_list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        Fragment fragment = DocumentsFragment.newInstance(null);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        VkDocument.ExtType extType;
        switch (position) {
            case 1: extType = VkDocument.ExtType.TEXT; break;
            case 2: extType = VkDocument.ExtType.BOOK; break;
            case 3: extType = VkDocument.ExtType.ARCHIVE; break;
            case 4: extType = VkDocument.ExtType.GIF; break;
            case 5: extType = VkDocument.ExtType.AUDIO; break;
            case 6: extType = VkDocument.ExtType.VIDEO; break;
            case 7: extType = VkDocument.ExtType.UNKNOWN; break;
            default: extType = null;
        }

        Fragment fragment = DocumentsFragment.newInstance(extType);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing if no filter category was selected.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.documents_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by:
                return true;
            case R.id.action_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

