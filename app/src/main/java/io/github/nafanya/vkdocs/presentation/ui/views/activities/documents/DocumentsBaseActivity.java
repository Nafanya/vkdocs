package io.github.nafanya.vkdocs.presentation.ui.views.activities.documents;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.ui.SortMode;
import io.github.nafanya.vkdocs.presentation.ui.views.dialogs.SortByDialog;
import io.github.nafanya.vkdocs.presentation.ui.views.fragments.documents.DocumentsListFragment;
import timber.log.Timber;

public abstract class DocumentsBaseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, SortByDialog.Callback, SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    @Bind(R.id.coordinator_layout) LinearLayout cooridnatorLayout;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.empty_view) RelativeLayout emptyView;
    @Bind(R.id.viewpager) ViewPager viewPager;
    @Bind(R.id.tab_layout) TabLayout tabLayout;

    private Drawer drawer;
    protected AccountHeader accountHeader;

    private static final String SORT_MODE_KEY = "sort_mode_arg";
    private static final String EXT_TYPE_KEY = "ext_type_key";
    private static final String NAV_DRAW_POS = "nav_drawer_pos_key";
    private static final String REFRESH_KEY = "refreshing_key";
    private static final String SEARCH_FILTER_KEY = "search_filter_key";

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

        initNavigationDrawer();
        initViewPager();
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
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.clear(imageView);
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
                        new PrimaryDrawerItem()
                                .withName(R.string.tab_all)
                                .withIcon(R.drawable.ic_folder)
                                .withSelectedTextColorRes(R.color.m_selected_text_all)
                                .withIconColorRes(R.color.m_icon_all),
                        new PrimaryDrawerItem()
                                .withName(R.string.tab_text)
                                .withIcon(R.drawable.book_open_variant)
                                .withSelectedTextColorRes(R.color.m_selected_text_text)
                                .withIconColorRes(R.color.m_icon_text),
                        new PrimaryDrawerItem()
                                .withName(R.string.tab_archives)
                                .withIcon(R.drawable.zip_box)
                                .withSelectedTextColorRes(R.color.m_selected_text_archive)
                                .withIconColorRes(R.color.m_icon_archive),
                        new PrimaryDrawerItem()
                                .withName(R.string.tab_images)
                                .withIcon(R.drawable.image)
                                .withSelectedTextColorRes(R.color.m_selected_text_image)
                                .withIconColorRes(R.color.m_icon_image),
                        new PrimaryDrawerItem()
                                .withName(R.string.tab_gifs)
                                .withIcon(R.drawable.image_vintage)
                                .withSelectedTextColorRes(R.color.m_selected_text_gif)
                                .withIconColorRes(R.color.m_icon_gif),
                        new PrimaryDrawerItem()
                                .withName(R.string.tab_music)
                                .withIcon(R.drawable.music_box)
                                .withSelectedTextColorRes(R.color.m_selected_text_music)
                                .withIconColorRes(R.color.m_icon_music),
                        new PrimaryDrawerItem()
                                .withName(R.string.tab_video)
                                .withIcon(R.drawable.movie)
                                .withSelectedTextColorRes(R.color.m_selected_text_video)
                                .withIconColorRes(R.color.m_icon_video),
                        new PrimaryDrawerItem()
                                .withName(R.string.tab_other)
                                .withIcon(R.drawable.file_multiple)
                                .withSelectedTextColorRes(R.color.m_selected_text_other)
                                .withIconColorRes(R.color.m_icon_other),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.drawer_settings).withIcon(R.drawable.ic_settings).
                                withSelectedTextColorRes(R.color.selectedItem).withSelectedIcon(R.drawable.ic_settings_selected))
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    Timber.d("[Documents activity] Nav drawer position: %d", position);
                    drawer.closeDrawer();
                    if (navDrawerPos != position) {
                        navDrawerPos = position;
                        onSectionChanged(navDrawerPos);
                    }
                    return true;
                }).build();
        drawer.setSelectionAtPosition(navDrawerPos);
        drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
    }

    private void initViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new DummyFragment(getResources().getColor(R.color.accent_material_light)), "CAT");
        adapter.addFrag(new DummyFragment(getResources().getColor(R.color.ripple_material_light)), "DOG");
        adapter.addFrag(new DummyFragment(getResources().getColor(R.color.button_material_dark)), "MOUSE");
        viewPager.setAdapter(adapter);
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

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1:
                    return DocumentsListFragment.new
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}

