package io.github.nafanya.vkdocs.presentation.ui.views;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.ArrayList;
import java.util.List;

import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.data.DocumentRepositoryImpl;
import io.github.nafanya.vkdocs.data.database.mapper.DbToDomainMapper;
import io.github.nafanya.vkdocs.data.database.repository.DatabaseRepository;
import io.github.nafanya.vkdocs.data.database.repository.DatabaseRepositoryImpl;
import io.github.nafanya.vkdocs.data.net.NetworkRepository;
import io.github.nafanya.vkdocs.data.net.NetworkRepositoryImpl;
import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import io.github.nafanya.vkdocs.net.InternetServiceImpl;
import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DocumentsActivity extends ListActivity implements DocumentsPresenter.Callback {
    public final String TAG = this.getClass().toString();

    private EventBus eventBus;
    private DocumentsPresenter documentsPresenter;
    private DocumentRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);
        setListAdapter(null);

        eventBus = ((App)getApplication()).getEventBus();
        DatabaseRepository databaseRepository = new DatabaseRepositoryImpl(new DbToDomainMapper());
        NetworkRepository networkRepository = new NetworkRepositoryImpl(new InternetServiceImpl());
        repository = new DocumentRepositoryImpl(databaseRepository, networkRepository);

        documentsPresenter = new DocumentsPresenter(
                AndroidSchedulers.mainThread(),
                Schedulers.newThread(),
                eventBus,
                repository);
        documentsPresenter.setCallback(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        documentsPresenter.loadDatabaseDocuments();
        //documentsPresenter.loadNetworkDocuments();
    }

    @Override
    protected void onStop() {
        super.onStop();
        documentsPresenter.onStop();
    }

    @Override
    public void onDatabaseDocuments(List<VKApiDocument> documents) {
        List<String> docs = new ArrayList<>();
        for (int i = 0; i < documents.size(); ++i)
            docs.add(documents.get(i).title);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(DocumentsActivity.this,
                android.R.layout.simple_list_item_1, docs);
        setListAdapter(adapter);
    }

    @Override
    public void onNetworkDocuments(List<VKApiDocument> documents) {
        List<String> docs = new ArrayList<>();
        for (VKApiDocument doc : documents)
            docs.add(doc.title);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(DocumentsActivity.this,
                android.R.layout.simple_list_item_1, docs);
        setListAdapter(adapter);
    }

    @Override
    public void onNetworkError(Exception ex) {
        Log.d("NETWORK", "naher shel");
    }

    @Override
    public void onDatabaseError(Exception ex) {
        Log.d("DB", "naher shel from db");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "on destroy!!");
    }
}
