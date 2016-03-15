package io.github.nafanya.vkdocs.presentation.ui.views.activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.SortMode;
import io.github.nafanya.vkdocs.presentation.ui.adapters.SpinnerAdapter;
import io.github.nafanya.vkdocs.presentation.ui.decorators.EndOffsetItemDecorator;
import io.github.nafanya.vkdocs.presentation.ui.decorators.SimpleDivierItermDecorator;
import io.github.nafanya.vkdocs.presentation.ui.views.dialogs.SortByDialogFragment;
import timber.log.Timber;

public abstract class BaseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, SortByDialogFragment.Callback {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.toolbar_spinner)
    Spinner spinner;
    @Bind(R.id.list_documents)
    RecyclerView recyclerView;

    private Drawer drawer;

    private String SORT_MODE_KEY = "sort_mode_arg";
    private String EXT_TYPE_KEY = "ext_type_key";
    private String NAV_DRAW_POS = "nav_drawer_pos";
    protected SortMode sortMode = SortMode.DATE;
    protected VkDocument.ExtType extType;
    protected int navDrawerPos = 1;//TODO make enum Section

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (state != null) {
            sortMode = (SortMode)state.getSerializable(SORT_MODE_KEY);
            extType = (VkDocument.ExtType)state.getSerializable(EXT_TYPE_KEY);
            navDrawerPos = state.getInt(NAV_DRAW_POS);
        }

        setContentView(R.layout.activity_documents);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        List<String> spinnerItems = Arrays.asList(getResources().getStringArray(R.array.doc_type_filter_items));
        SpinnerAdapter adapter = new SpinnerAdapter(this);
        adapter.addItems(spinnerItems);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        initRecyclerView();

        initNavigationDrawer(navDrawerPos);
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SimpleDivierItermDecorator(this));
        // Convert dp to px
        final int px = (int) (this.getResources().getDimension(R.dimen.recyclerview_bottom_padding) * getResources().getDisplayMetrics().density);
        recyclerView.addItemDecoration(new EndOffsetItemDecorator(px));

    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putSerializable(SORT_MODE_KEY, sortMode);
        state.putSerializable(EXT_TYPE_KEY, extType);
        state.putInt(NAV_DRAW_POS, navDrawerPos);
        super.onSaveInstanceState(state);
    }

    private void initNavigationDrawer(int restorePosition) {
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_my_documents).withIcon(R.drawable.ic_folder),
                        new PrimaryDrawerItem().withName(R.string.drawer_offline).withIcon(R.drawable.ic_offline),
                        new PrimaryDrawerItem().withName(R.string.drawer_uploads).withIcon(R.drawable.ic_upload),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.drawer_settings).withIcon(R.drawable.ic_settings))
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    Timber.d("[Documents activity] Nav drawer position: %d", position);
                    drawer.closeDrawer();
                    if (navDrawerPos != position) {
                        navDrawerPos = position;
                        onSectionChanged(navDrawerPos, extType, sortMode);
                    }
                    return true;
                }).build();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        drawer.setSelectionAtPosition(restorePosition);
        drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
    }


    /*** Spinner callbacks***/
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        VkDocument.ExtType newExtType;
        switch (position) {
            case 1: newExtType = VkDocument.ExtType.TEXT; break;
            case 2: newExtType = VkDocument.ExtType.BOOK; break;
            case 3: newExtType = VkDocument.ExtType.ARCHIVE; break;
            case 4: newExtType = VkDocument.ExtType.GIF; break;
            case 5: newExtType = VkDocument.ExtType.AUDIO; break;
            case 6: newExtType = VkDocument.ExtType.VIDEO; break;
            case 7: newExtType = VkDocument.ExtType.UNKNOWN; break;
            default: newExtType = null;
        }
        if (newExtType != extType)
            onExtensionChanged(newExtType);
        extType = newExtType;

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing if no filter category was selected.
    }

    /***Menu callbacks***/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.documents_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by:
                showSortByDialog(sortMode);
                return true;
            case R.id.action_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSortByDialog(SortMode currentSortMode) {
        DialogFragment dialog = SortByDialogFragment.create(currentSortMode);
        dialog.show(getSupportFragmentManager(), "sortmode");
    }

    /*** Sort dialog callbacks***/
    @Override
    public void onSortModeChanged(SortMode newSortMode) {
        sortMode = newSortMode;
    }

    public abstract void onExtensionChanged(VkDocument.ExtType newExtType);
    public abstract void onSectionChanged(int newPos, VkDocument.ExtType extType, SortMode sortMode);
}

