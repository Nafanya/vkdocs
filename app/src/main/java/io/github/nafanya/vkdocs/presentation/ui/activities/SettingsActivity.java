package io.github.nafanya.vkdocs.presentation.ui.activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.domain.model.DocumentsInfo;
import io.github.nafanya.vkdocs.presentation.presenter.SettingsPresenter;
import io.github.nafanya.vkdocs.presentation.ui.dialogs.CacheSizePicker;
import io.github.nafanya.vkdocs.presentation.ui.dialogs.ConfirmationDialog;
import io.github.nafanya.vkdocs.utils.FileFormatter;

public class SettingsActivity extends AppCompatActivity
        implements ConfirmationDialog.Callback, CacheSizePicker.OnPickCacheSize {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.total_documents)
    TextView totalDocuments;

    @Bind(R.id.cache_statistic)
    TextView cacheStatistic;

    @Bind(R.id.offline_statistic)
    TextView offlineStatistic;

    @Bind(R.id.current_cache_size)
    TextView currentCacheSize;

    private SettingsPresenter presenter;
    private FileFormatter fileFormatter;

    private String getFiles(int files) {
        String file;
        if (files % 10 == 1)
            file = getResources().getString(R.string.file1_label);//файл
        else if (files % 10 >= 2 && files % 10 <= 4 && files % 100 / 10 != 1)
            file = getResources().getString(R.string.file24_label);//файла
        else
            file = getResources().getString(R.string.files);//файлов
        return files + " " + file;
    }

    private String getStatisticLabel(DocumentsInfo info) {
        return getFiles(info.totalFiles) + "/" + fileFormatter.formatSize(info.totalSize);
    }

    private CharSequence cacheSizeLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cacheSizeLabel = currentCacheSize.getText();

        App app = (App)getApplication();
        presenter = new SettingsPresenter(app.getOfflineManager(), app.getCacheManager());
        fileFormatter = app.getFileFormatter();

        totalDocuments.setText(getFiles(presenter.getTotalFiles()));
        offlineStatistic.setText(getStatisticLabel(presenter.getOfflineInfo()));
        cacheStatistic.setText(getStatisticLabel(presenter.getCacheInfo()));
        String cacheSize = presenter.getCacheSize() + "MB";
        currentCacheSize.setText(cacheSizeLabel + cacheSize);
    }

    private static final int CACHE_CLEAR = 0;
    private static final int OFFLINE_CLEAR = 1;

    @OnClick(R.id.cache_size)
    void onClickCacheSize(View v) {
        int currentCacheSize = presenter.getCacheSize();
        DialogFragment dialog = CacheSizePicker.newInstance(currentCacheSize);
        dialog.show(getSupportFragmentManager(), "cache_size");
    }

    @Override
    public void onPick(int cacheSize) {
        presenter.setCacheSize(cacheSize);
        String cacheSizeStr = cacheSize + "MB";
        currentCacheSize.setText(cacheSizeLabel + cacheSizeStr);
    }

    @OnClick(R.id.clear_cache)
    void onClickClearCache(View v) {
        DialogFragment dialog = ConfirmationDialog.newInstance(CACHE_CLEAR,
                getResources().getString(R.string.clear_cache_title),
                getResources().getString(R.string.clear_cache_message));
        dialog.show(getSupportFragmentManager(), "clear_cache");
    }

    @OnClick(R.id.clear_offline)
    void onClickClearOffline(View v) {
        DialogFragment dialog = ConfirmationDialog.newInstance(OFFLINE_CLEAR,
                getResources().getString(R.string.clear_offline_title),
                getResources().getString(R.string.clear_offline_message));
        dialog.show(getSupportFragmentManager(), "clear_offline");
    }

    private static final DocumentsInfo EMPTY_INFO = new DocumentsInfo(0, 0);

    @Override
    public void onConfirm(int label) {
        if (label == CACHE_CLEAR) {
            presenter.clearCache();
            cacheStatistic.setText(getStatisticLabel(EMPTY_INFO));
        } else {
            presenter.clearOffline();
            offlineStatistic.setText(getStatisticLabel(EMPTY_INFO));
        }
    }
}
