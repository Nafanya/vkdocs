package io.github.nafanya.vkdocs.presentation.ui.views.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.presentation.presenter.SettingsPresenter;

public class SettingsActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private SettingsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        App app = (App)getApplication();
        presenter = new SettingsPresenter(
                app.getOfflineManager(),
                app.getCacheManager());
    }


}
