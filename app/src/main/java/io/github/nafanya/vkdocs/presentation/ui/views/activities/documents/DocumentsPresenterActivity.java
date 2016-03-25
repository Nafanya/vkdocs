package io.github.nafanya.vkdocs.presentation.ui.views.activities.documents;

import android.support.v7.app.AppCompatActivity;

import io.github.nafanya.vkdocs.presentation.presenter.DocumentsPresenter;

/**
 * Created by pva701 on 15.03.16.
 */
public abstract class DocumentsPresenterActivity extends AppCompatActivity implements DocumentsPresenter.Callback {
/*
    protected DocumentsPresenter presenter;
//    protected BaseSortedAdapter adapter;
    protected FileFormatter fileFormatter;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        App app = (App)getApplication();
        fileFormatter = app.getFileFormatter();

        presenter = new DocumentsPresenter(
                getFilter(navDrawerPos, documentType),
                app.getEventBus(),
                app.getRepository(),
                app.getDownloadManager(),
                app.getOfflineManager(),
                app.getAppCacheRoot(),
                app.getAppOfflineRoot(),
                (DownloadManager) getSystemService(DOWNLOAD_SERVICE),
                app.getUserRepository(),
                this);
        presenter.getUserInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Timber.d("ON START PRESENTER ACTIVITY");
        presenter.onStart();
        presenter.getDocuments();
    }

    @Override
    protected void onStop() {
        presenter.onStop();
        super.onStop();
    }

    protected DocFilter getFilter(int section, VkDocument.ExtType documentType) {
        if (section == 1) {
            if (documentType == null)
                return ExtDocFilter.ALL;
            return new ExtDocFilter(documentType);
        } else {
            if (documentType == null)
                return OfflineDocFilter.ALL;
            return new OfflineDocFilter(documentType);
        }
    }

    protected abstract BaseSortedAdapter newAdapter();

    private void updateData(List<VkDocument> documents) {
    }

    @Override
    public void onGetDocuments(List<VkDocument> documents) {
        Timber.d("ON GET DOCUMENTS " + documents.size());
        updateData(documents);
    }

    @Override
    public void onNetworkDocuments(List<VkDocument> documents) {
        Timber.d("ON NETWORK DOCUMENTS");
        updateData(documents);
    }

    @Override
    public void onNetworkError(Exception ex) {
        Snackbar snackbar = Snackbar
                .make(cooridnatorLayout, "No internet connection", Snackbar.LENGTH_LONG)
                .setAction("RETRY", view -> {
                    Timber.d("retry refresh");
                    onRefresh();
                });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    @Override
    public void onDatabaseError(Exception ex) {

    }

    protected void openDocument(VkDocument document) {
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String mimeType = myMime.getMimeTypeFromExtension(document.getExt());
        Timber.d("[openDocument] path = %s", document.getPath());
        File fileDoc = new File(document.getPath());
        newIntent.setDataAndType(Uri.fromFile(fileDoc), mimeType);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//TODO one task?
        try {
            startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            //TODO do something
            //Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUserInfoLoaded(VKApiUser userInfo) {
        Timber.d("info " + userInfo.first_name + " " + userInfo.last_name + " image = " + userInfo.photo_100);
        String fullName;
        if (userInfo.first_name == null && userInfo.last_name == null)
            fullName = "Unknown";
        else if (userInfo.first_name == null)
            fullName = userInfo.last_name;
        else if (userInfo.last_name == null)
            fullName = userInfo.first_name;
        else
            fullName = userInfo.first_name + " " + userInfo.last_name;

        ProfileDrawerItem account = new ProfileDrawerItem()
                .withName(fullName)
                .withIcon(userInfo.photo_100);

        accountHeader.clear();
        accountHeader.addProfile(account, 0);
        //accountHeader.setBackground(new BitmapDrawable(getResources(), av));


        /*Timber.d("pre draw");
        //Drawable av = BitmapDrawable.createFromPath(userInfo.photo_100);
        int w = accountHeader.getView().getWidth();
        int h = accountHeader.getView().getHeight();
        Timber.d("l1");
        Bitmap av = BitmapFactory.decodeFile(userInfo.photo_100);
        Timber.d("l2");
        av = Bitmap.createScaledBitmap(av, w, h, false);
        Timber.d("l3");

        FastBlur.fastBlur(av, 5).subscribe(new DefaultSubscriber<Bitmap>() {
            @Override
            public void onNext(Bitmap av) {
                Timber.d("on next");
            }
        });
    }*/
}
