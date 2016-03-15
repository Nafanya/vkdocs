package io.github.nafanya.vkdocs.presentation.ui.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.SortMode;
import io.github.nafanya.vkdocs.presentation.ui.adapters.SpinnerAdapter;

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

        List<String> spinnerItems = Arrays.asList(getResources().getStringArray(R.array.doc_type_filter_items));
        SpinnerAdapter adapter = new SpinnerAdapter(this);
        adapter.addItems(spinnerItems);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        Fragment fragment = DocumentsFragment.newInstance(null, SortMode.DATE);

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

        final SortMode sortMode;
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof DocumentsFragment) {
            sortMode = ((DocumentsFragment) currentFragment).getSortMode();
        } else {
            sortMode = SortMode.DATE;
        }
        Fragment fragment = DocumentsFragment.newInstance(extType, sortMode);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing if no filter category was selected.
    }
}

