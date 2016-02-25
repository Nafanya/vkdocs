package io.github.nafanya.vkdocs.presentation.ui.views;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.vk.sdk.api.model.VKApiDocument;
import com.vk.sdk.util.VKUtil;

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
import io.github.nafanya.vkdocs.domain.interactor.DeleteDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import io.github.nafanya.vkdocs.net.InternetServiceImpl;
import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.DocumentAdapter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class DocumentsActivity extends Activity
        implements DocumentsPresenter.Callback, DocumentAdapter.DocumentViewHolder.DocumentClickListener {

    private EventBus eventBus;
    private DocumentsPresenter documentsPresenter;
    private DocumentRepository repository;
    private DocumentAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        eventBus = ((App)getApplication()).getEventBus();
        DatabaseRepository databaseRepository = new DatabaseRepositoryImpl(new DbToDomainMapper());
        NetworkRepository networkRepository = new NetworkRepositoryImpl(new InternetServiceImpl());
        repository = new DocumentRepositoryImpl(databaseRepository, networkRepository);

        recyclerView = (RecyclerView)findViewById(R.id.document_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        documentsPresenter = new DocumentsPresenter(eventBus, repository, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Timber.d("CR MENU");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.documents_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         int id = item.getItemId();

        if (id == R.id.action_refresh) {
            documentsPresenter.loadNetworkDocuments();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        documentsPresenter.loadNetworkDocuments();
    }

    @Override
    protected void onStop() {
        super.onStop();
        documentsPresenter.onStop();
    }

    @Override
    public void onGetDocuments(List<VKApiDocument> documents) {
        if (adapter == null) {
            adapter = new DocumentAdapter(this);
            recyclerView.setAdapter(adapter);
        }
        adapter.setData(documents);
    }

    @Override
    public void onNetworkError(Exception ex) {
        Timber.d("network error");
    }

    @Override
    public void onDatabaseError(Exception ex) {
        Timber.d("db error");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.d("on destroy");
    }

    @Override
    public void onClickDelete(int position) {
        Timber.d("ON CLICK DELETE");
        new DeleteDocument(AndroidSchedulers.mainThread(), Schedulers.io(),
                eventBus, false, repository, adapter.getItem(position)).execute();
        adapter.removeIndex(position);
    }
}
