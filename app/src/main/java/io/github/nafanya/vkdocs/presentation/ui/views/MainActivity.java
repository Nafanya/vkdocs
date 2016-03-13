package io.github.nafanya.vkdocs.presentation.ui.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.utils.FileFormatter;
import timber.log.Timber;

import static io.github.nafanya.vkdocs.domain.model.VkDocument.ExtType;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.tab_layout) TabLayout tabLayout;
    @Bind(R.id.pager) ViewPager pager;

    private Drawer drawer;
    private CharSequence title;

    private String[] tabs;
    private DocumentPagerAdapter adapter;

    private Fragment[] tabFragments = new Fragment[]{
            MyDocsListFragment.newInstance(null),
            MyDocsListFragment.newInstance(ExtType.TEXT),
            MyDocsListFragment.newInstance(ExtType.BOOK),
            MyDocsListFragment.newInstance(ExtType.ARCHIVE),
            MyDocsListFragment.newInstance(ExtType.GIF),
            MyDocsListFragment.newInstance(ExtType.IMAGE),
            MyDocsListFragment.newInstance(ExtType.AUDIO),
            MyDocsListFragment.newInstance(ExtType.VIDEO),
            MyDocsListFragment.newInstance(ExtType.UNKNOWN),
    };

    private Fragment[] offlineFragments = new Fragment[]{
            OfflineListFragment.newInstance(null),
            OfflineListFragment.newInstance(ExtType.TEXT),
            OfflineListFragment.newInstance(ExtType.BOOK),
            OfflineListFragment.newInstance(ExtType.ARCHIVE),
            OfflineListFragment.newInstance(ExtType.GIF),
            OfflineListFragment.newInstance(ExtType.IMAGE),
            OfflineListFragment.newInstance(ExtType.AUDIO),
            OfflineListFragment.newInstance(ExtType.VIDEO),
            OfflineListFragment.newInstance(ExtType.UNKNOWN),
    };

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initUI();
        initNavigationDrawer();
    }

    private void initUI() {
        // Toolbar
        setSupportActionBar(toolbar);

        tabs = getResources().getStringArray(R.array.tabs);
        adapter = new DocumentPagerAdapter(getSupportFragmentManager());

        for (int i = 0; i < tabs.length; ++i)
            adapter.addFragment(tabs[i], tabFragments[i]);

        pager.setAdapter(adapter);

        for (String title : tabs)
            tabLayout.addTab(tabLayout.newTab().setText(title));


        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(pager);
    }

    private void initNavigationDrawer() {
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_my_documents).withIcon(R.drawable.ic_folder),
                        new PrimaryDrawerItem().withName(R.string.drawer_offline).withIcon(R.drawable.ic_offline),
                        new PrimaryDrawerItem().withName(R.string.drawer_uploads).withIcon(R.drawable.ic_upload),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.drawer_settings).withIcon(R.drawable.ic_settings))
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    Timber.d("nav drawer pos " + position);

                    Fragment[] tabsF;
                    if (position == 1)
                        tabsF = tabFragments;
                    else
                        tabsF = offlineFragments;

                    adapter.clear();
                    for (int i = 0; i < tabs.length; ++i)
                        adapter.addFragment(tabs[i], tabsF[i]);
                    adapter.notifyDataSetChanged();

                    drawer.closeDrawer();
                    return true;
                })
                .build();
    }

    private static class DocumentPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentTitles = new ArrayList<>();

        public DocumentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(String title, Fragment fragment) {
            fragments.add(fragment);
            fragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragmentTitles.size();
        }

        @Override
        public String getPageTitle(int position) {
            return fragmentTitles.get(position);
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        public void clear() {
            fragments.clear();
            fragmentTitles.clear();
        }
    }
}
