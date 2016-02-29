package io.github.nafanya.vkdocs;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
//import android.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.presentation.ui.views.docs.TabbedDocsFragment;

public class MainActivity extends AppCompatActivity {
    /*@Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Bind(R.id.left_drawer)
    ListView drawerList;*/

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private Drawer drawer;
    private String[] drawerItems;
    private CharSequence title;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        drawerItems = getResources().getStringArray(R.array.drawer_items);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(drawerItems[0]),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(drawerItems[1]),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(drawerItems[2]))
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {

                    Fragment fragment = new TabbedDocsFragment();
                    //fragment.setArguments();

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                    drawer.closeDrawer();
                    return true;
                    // do something with the clicked item :D
                })
                .build();
    }
}
