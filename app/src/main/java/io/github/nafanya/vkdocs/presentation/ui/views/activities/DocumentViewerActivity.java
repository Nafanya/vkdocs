package io.github.nafanya.vkdocs.presentation.ui.views.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.presentation.services.AudioPlayerService;
import io.github.nafanya.vkdocs.presentation.ui.adapters.DocumentsPagerAdapter;
import io.github.nafanya.vkdocs.presentation.ui.views.fragments.MusicPlayFragment;
import timber.log.Timber;

public class DocumentViewerActivity extends AppCompatActivity implements MusicPlayFragment.Player {
    public static String POSITION_KEY = "position_key";
    public static String DOCUMENTS_KEY = "documents_key";

    private DocumentsPagerAdapter documentsPagerAdapter;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.container)
    ViewPager viewPager;

    private AudioPlayerService playerService;
    private ServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_document_viewer);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (state == null)
            state = getIntent().getExtras();
        int position = state.getInt(POSITION_KEY);
        List<VkDocument> documents = state.getParcelableArrayList(DOCUMENTS_KEY);
        //Timber.d("pos = " + position);
        //Timber.d("docs = " + documents.get(0).title + " " + documents.get(1).title);

        documentsPagerAdapter = new DocumentsPagerAdapter(getSupportFragmentManager(), documents);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Timber.d("on service connected");
                playerService = ((AudioPlayerService.AudioPlayerBinder) service).service();
                viewPager.setAdapter(documentsPagerAdapter);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                playerService = null;
            }
        };
        Intent intent = new Intent(this, AudioPlayerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        Timber.d("bind service");

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                playerService.stop();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        viewPager.setCurrentItem(position);
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

    @Override
    public AudioPlayerService playerService() {
        return playerService;
    }

    @Override
    protected void onDestroy() {
        playerService.stop();
        unbindService(serviceConnection);
        super.onDestroy();
    }
}
