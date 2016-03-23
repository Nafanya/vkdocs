package io.github.nafanya.vkdocs.presentation.ui.views.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.SortMode;
import io.github.nafanya.vkdocs.presentation.ui.adapters.SpinnerAdapter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.base.EmptyRecyclerView;
import io.github.nafanya.vkdocs.presentation.ui.decorators.EndOffsetItemDecorator;
import io.github.nafanya.vkdocs.presentation.ui.decorators.SimpleDivierItermDecorator;
import io.github.nafanya.vkdocs.presentation.ui.views.dialogs.SortByDialog;
import timber.log.Timber;

public abstract class BaseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, SortByDialog.Callback, SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    @Bind(R.id.coordinator_layout)
    LinearLayout cooridnatorLayout;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.toolbar_spinner)
    Spinner spinner;

    @Bind(R.id.list_documents)
    EmptyRecyclerView recyclerView;

    @Bind(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.empty_view)
    RelativeLayout emptyView;

    private Drawer drawer;
    protected AccountHeader accountHeader;

    private String SORT_MODE_KEY = "sort_mode_arg";
    private String EXT_TYPE_KEY = "ext_type_key";
    private String NAV_DRAW_POS = "nav_drawer_pos_key";
    private String REFRESH_KEY = "refreshing_key";
    private String SEARCH_FILTER_KEY = "search_filter_key";

    protected SortMode sortMode = SortMode.DATE;
    protected VkDocument.ExtType extType;
    protected String searchFilter = "";
    protected int navDrawerPos = 1;//TODO make enum Section
    private boolean isRefreshing;
    private boolean storagePermissionGranted = Build.VERSION.SDK_INT < 23;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (state != null) {
            sortMode = (SortMode)state.getSerializable(SORT_MODE_KEY);
            extType = (VkDocument.ExtType)state.getSerializable(EXT_TYPE_KEY);
            navDrawerPos = state.getInt(NAV_DRAW_POS);
            isRefreshing = state.getBoolean(REFRESH_KEY);
            searchFilter = state.getString(SEARCH_FILTER_KEY);
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
        initNavigationDrawer();
        initSwipeRefreshLayout();
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(isRefreshing));
        //setRefresh(isRefreshing);
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SimpleDivierItermDecorator(this));
        recyclerView.setEmptyView(emptyView);
        // Convert dp to px
        final int px = (int) (this.getResources().getDimension(R.dimen.recyclerview_bottom_padding) * getResources().getDisplayMetrics().density);
        recyclerView.addItemDecoration(new EndOffsetItemDecorator(px));

    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putSerializable(SORT_MODE_KEY, sortMode);
        state.putSerializable(EXT_TYPE_KEY, extType);
        state.putInt(NAV_DRAW_POS, navDrawerPos);
        state.putBoolean(REFRESH_KEY, isRefreshing);
        state.putString(SEARCH_FILTER_KEY, searchFilter);
        super.onSaveInstanceState(state);
    }

    private void initNavigationDrawer() {
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }
            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }
        });

        accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .build();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(accountHeader)
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
                        onSectionChanged(navDrawerPos);
                    }
                    return true;
                }).build();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        drawer.setSelectionAtPosition(navDrawerPos);
        drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
    }

    //TODO fix infinite refreshing
    protected void setRefresh(boolean isRef) {
        isRefreshing = isRef;
        //swipeRefreshLayout.setRefreshing(isRefreshing);
        swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(isRefreshing));
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
            case 5: newExtType = VkDocument.ExtType.IMAGE; break;
            case 6: newExtType = VkDocument.ExtType.AUDIO; break;
            case 7: newExtType = VkDocument.ExtType.VIDEO; break;
            case 8: newExtType = VkDocument.ExtType.UNKNOWN; break;
            default: newExtType = null;
        }
        if (newExtType != extType)
            onTypeFilterChanged(newExtType);
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
        searchView.setOnQueryTextListener(this);
        // The only valid fix to make SearchView expand full width
        searchView.setMaxWidth(Integer.MAX_VALUE);
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
        DialogFragment dialog = SortByDialog.create(currentSortMode);
        dialog.show(getSupportFragmentManager(), "sortmode");
    }

    /*** Sort dialog callbacks***/
    @Override
    public void onSortModeChanged(SortMode newSortMode) {
        sortMode = newSortMode;
    }

    public abstract void onTypeFilterChanged(VkDocument.ExtType newExtType);
    public abstract void onSectionChanged(int newPos);

    /*** SerachView callbacks ***/
    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchFilter = newText;
        return true;
    }

    /*** Permissions check ***/
    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
//            permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0]== PackageManager.PERMISSION_GRANTED){
            storagePermissionGranted = true;
        }
    }
}

