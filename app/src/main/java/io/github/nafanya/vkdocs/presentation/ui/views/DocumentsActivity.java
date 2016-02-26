package io.github.nafanya.vkdocs.presentation.ui.views;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.nafanya.vkdocs.App;
import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.data.DocumentRepositoryImpl;
import io.github.nafanya.vkdocs.data.database.mapper.DocsMapper;
import io.github.nafanya.vkdocs.data.database.repository.DatabaseRepository;
import io.github.nafanya.vkdocs.data.database.repository.DatabaseRepositoryImpl;
import io.github.nafanya.vkdocs.data.net.NetworkRepository;
import io.github.nafanya.vkdocs.data.net.NetworkRepositoryImpl;
import io.github.nafanya.vkdocs.domain.download.DownloadRequest;
import io.github.nafanya.vkdocs.domain.download.InterruptableDownloadManager;
import io.github.nafanya.vkdocs.domain.download.base.DownloadManager;
import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.DeleteDocument;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;
import io.github.nafanya.vkdocs.net.InternetServiceImpl;
import io.github.nafanya.vkdocs.presentation.presenter.base.DocumentsPresenter;
import io.github.nafanya.vkdocs.presentation.ui.adapters.DocumentAdapter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class DocumentsActivity extends AppCompatActivity
        implements DocumentsPresenter.Callback, DocumentAdapter.DocumentViewHolder.DocumentClickListener {

    private EventBus eventBus;
    private InterruptableDownloadManager downloadManager;

    private DocumentsPresenter documentsPresenter;
    private DocumentRepository repository;
    private DocumentAdapter adapter;

    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @Bind(R.id.list_documents)
    RecyclerView recyclerView;

//    @Bind(R.id.toolbar)
//    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        ButterKnife.bind(this);

//        setSupportActionBar(toolbar);
//        toolbar.setTitle(getTitle());
        setTitle(R.string.title_activity_documents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventBus = ((App)getApplication()).getEventBus();
        downloadManager = ((App)getApplication()).getDownloadManager();

        /*DownloadRequest request = new DownloadRequest(
                "http://www.cimec.org.ar/twiki/pub/Cimec/GeometriaComputacional/DeBerg_-_Computational_Geometry_-_Algorithms_and_Applications_2e.pdf",
                "/sdcard/aaaa_de_berg.pdf", new DownloadManager.RequestObserver() {
            @Override
            public void onProgress(int percentage) {
                Timber.d("progress downloading: %s perc", percentage);
            }

            @Override
            public void onComplete() {
                Timber.d("on complete downloading");
            }

            @Override
            public void onError(Exception e) {
                Timber.d("download exception");
                e.printStackTrace();
            }

            @Override
            public void onInfiniteProgress() {
                Timber.d("infinite progress");
            }
        });
        downloadManager.enqueue(request);*/

        DownloadRequest request = downloadManager.getQueue().get(1);
        request.setObserver(new DownloadManager.RequestObserver() {
            @Override
            public void onProgress(int percentage) {
                Timber.d("progress downloading: %s perc", percentage);
            }

            @Override
            public void onComplete() {
                Timber.d("on complete downloading");
            }

            @Override
            public void onError(Exception e) {
                Timber.d("download exception");
                e.printStackTrace();
            }

            @Override
            public void onInfiniteProgress() {
                Timber.d("infinite progress");
            }
        });
        downloadManager.retry(request);

        DatabaseRepository databaseRepository = new DatabaseRepositoryImpl(new DocsMapper());
        NetworkRepository networkRepository = new NetworkRepositoryImpl(new InternetServiceImpl());
        repository = new DocumentRepositoryImpl(databaseRepository, networkRepository);

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
        documentsPresenter.loadDatabaseDocuments();
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
