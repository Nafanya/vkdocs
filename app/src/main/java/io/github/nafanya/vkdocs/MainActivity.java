package io.github.nafanya.vkdocs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.raizlabs.android.dbflow.annotation.NotNull;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.tab_layout) TabLayout tabLayout;
    @Bind(R.id.pager) ViewPager pager;

    private Drawer drawer;
    private CharSequence title;
    private ActionBarDrawerToggle actionBarDrawerToggle;

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

        pager.setAdapter(new DocumentPagerAdapter(getSupportFragmentManager(), tabs));

        for (String title : tabs) {
            tabLayout.addTab(tabLayout.newTab().setText(title));
        }
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
                        new PrimaryDrawerItem().withName(R.string.drawer_offline),
                        new PrimaryDrawerItem().withName(R.string.drawer_uploads).withIcon(R.drawable.ic_upload),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.drawer_settings).withIcon(R.drawable.ic_settings))
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {

//                    Fragment fragment = new TabbedDocsFragment();

//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                    drawer.closeDrawer();
                    return true;
                    // do something with the clicked item :D
                })
                .build();
    }

    private static class DocumentPagerAdapter extends FragmentPagerAdapter {

        private final String[] titles;

        public DocumentPagerAdapter(FragmentManager fm, @NonNull String[] titles) {
            super(fm);

            this.titles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            return OneFragment.createFragment(titles[position]);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public String getPageTitle(int position) {
            return titles[position];
        }
    }
}
