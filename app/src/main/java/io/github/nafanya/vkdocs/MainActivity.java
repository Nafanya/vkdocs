package io.github.nafanya.vkdocs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.presentation.ui.views.mydocs.tabs.AllListFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.mydocs.tabs.ArchivesListFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.mydocs.tabs.BooksListFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.mydocs.tabs.GifsListFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.mydocs.tabs.ImagesListFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.mydocs.tabs.MusicListFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.mydocs.tabs.OtherListFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.mydocs.tabs.TextListFragment;
import io.github.nafanya.vkdocs.presentation.ui.views.mydocs.tabs.VideoListFragment;


public class MainActivity extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.tab_layout) TabLayout tabLayout;
    @Bind(R.id.pager) ViewPager pager;

    private Drawer drawer;
    private CharSequence title;

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

        // Pager and tabs
        String[] tabs = getResources().getStringArray(R.array.tabs);

        DocumentPagerAdapter adapter = new DocumentPagerAdapter(getSupportFragmentManager());
        Fragment[] tabFragments = new Fragment[]{
                new AllListFragment(),
                new TextListFragment(),
                new BooksListFragment(),
                new ArchivesListFragment(),
                new GifsListFragment(),
                new ImagesListFragment(),
                new MusicListFragment(),
                new VideoListFragment(),
                new OtherListFragment()
        };

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

//                    Fragment fragment = new TabbedDocsFragment();

//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                    drawer.closeDrawer();
                    return true;
                })
                .build();
    }

    private static class DocumentPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentTitles = new ArrayList<>();

        //private final String[] titles;


        /*public DocumentPagerAdapter(FragmentManager fm, @NonNull String[] titles) {
            super(fm);

            this.titles = titles;
        }*/

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
    }
}
