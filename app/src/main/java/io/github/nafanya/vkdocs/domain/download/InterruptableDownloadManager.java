package io.github.nafanya.vkdocs.domain.download;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.github.nafanya.vkdocs.domain.download.base.DownloadManager;
import io.github.nafanya.vkdocs.domain.download.base.DownloadRequest;
import io.github.nafanya.vkdocs.domain.download.base.RequestStorage;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import timber.log.Timber;

public class InterruptableDownloadManager implements DownloadManager {
    private Scheduler workerScheduler;
    private RequestStorage<DownloadRequest> storage;
    private Set<DownloadRequest> memoryStorage = new TreeSet<>((lhs, rhs) -> lhs.getId() - rhs.getId());

    public InterruptableDownloadManager(Scheduler workScheduler, RequestStorage<DownloadRequest> storage) {
        this.workerScheduler = workScheduler;
        this.storage = storage;

        List<DownloadRequest> queue = storage.getAll();
        Timber.d("queue size %d", queue.size());
        for (DownloadRequest req: queue) {
            memoryStorage.add(req);
            Timber.d("down record " + req.getUrl() + " bytes: " + req.getBytes() + " from " + req.getTotalBytes() + ", perc = " + (req.getBytes() * 1.0 / req.getTotalBytes()));
        }
    }

    //public final int SAVE_EVERY_BYTES = 1024 * 1024; //bytes

    private class DownloadTask implements Observable.OnSubscribe<Integer> {
        private DownloadRequest request;
        private long fileLength;
        private int prevPercentage;
        private volatile boolean isRetry;

        public DownloadTask(DownloadRequest request, boolean isRetry) {
            this.request = request;
            this.isRetry = isRetry;
        }

        public DownloadTask(DownloadRequest request) {
            this.request = request;
        }

        public DownloadRequest getRequest() {
            return request;
        }

        @Override
        public void call(Subscriber<? super Integer> subscriber) {
            Timber.d("download task " + request.getUrl() + " " + request.getDocId() + " " + request.getDest());

            if (request.getTotalBytes() > 0)
                fileLength = request.getTotalBytes();

            if (isRetry && request.getId() == 0) {
                subscriber.onError(new RuntimeException("This request isn't executed yet!"));
                return;
            }

            request.setActive(true);
            Timber.d("in call idm = " + request.getId());
            HttpURLConnection connection = null;
            try {
                URL url = new URL(request.getUrl());
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestProperty("Range", "bytes=" + request.getBytes() + "-" );
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestMethod("GET");
                //connection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

                //connection.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
                //connection.setRequestProperty("Accept-Language", "ru,en-US;q=0.8,en;q=0.6");
                connection.setReadTimeout(20000);
                connection.setConnectTimeout(20000 + 5000);

                //connection.setUseCaches(false);
                //connection.setDoInput(true);
                //connection.setDoOutput(true);

                connection.connect();
                // expect HTTP 206 Partial content, so we don't mistakenly save error report instead of the file
                Timber.d("Server returned HTTP " +
                        connection.getResponseCode() + " " +
                        connection.getResponseMessage());

                if (connection.getResponseCode() != HttpURLConnection.HTTP_PARTIAL && connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    subscriber.onError(new RuntimeException("Server returned HTTP " +
                            connection.getResponseCode() + " " +
                            connection.getResponseMessage()));
                    return;
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                if (fileLength == 0) {
                    fileLength = connection.getContentLength();
                    request.setTotalBytes(fileLength);
                }

                // download the file
                InputStream input = connection.getInputStream();
                try {
                    RandomAccessFile output = new RandomAccessFile(request.getDest(), "rw"); //new FileOutputStream(request.getDest());

                    try {
                        byte data[] = new byte[4096];
                        long total = request.getBytes();
                        int count;
                        output.seek(total);

                        while ((count = input.read(data)) != -1) {
                            // allow canceling with back button
                            if (request.isCanceled()) {
                                deleteRequest(request);
                                //TODO write here remove file
                                input.close();
                                return;
                            }
                            total += count;
                            output.write(data, 0, count);
                            request.setBytes(total);
                            updateRequest(request);
                            publishProgress(subscriber, total);
                        }
                        deleteRequest(request);
                        subscriber.onCompleted();
                    } finally {
                        output.close();
                    }
                } finally {
                    input.close();
                }
            } catch (Exception e) {
                subscriber.onError(e);
            } finally {
                request.setActive(false);
                if (connection != null)
                    connection.disconnect();
            }
        }

        private void publishProgress(Subscriber<? super Integer> subscriber, long total) {
            if (fileLength > 0) {
                int perc = (int) (total * 100 / fileLength);
                if (perc != prevPercentage) {
                    subscriber.onNext(perc);
                    Timber.d("publish progress " + perc);
                }
                prevPercentage = perc;
            } else {
                if (prevPercentage != -1)
                    subscriber.onNext(-1);
                prevPercentage = -1;
            }
        }
    }

    public void cancelRequest(DownloadRequest request) {
        request.cancel();
        if (!request.isActive())
            deleteRequest(request);
    }

    private void updateRequest(DownloadRequest request) {
        storage.update(request);
    }

    private void deleteRequest(DownloadRequest request) {
        storage.delete(request);
        memoryStorage.remove(request);
        File file = new File(request.getDest());
        if (file.exists())
            file.delete();
    }

    private void insertRequest(DownloadRequest request) {
        storage.insert(request);
        memoryStorage.add(request);
    }

    public void retry(DownloadRequest request) {
        runTask(new DownloadTask(request, true));
    }


    @Override
    public void enqueue(final DownloadRequest request) {
        if (request.getId() != 0)
            throw new RuntimeException("This request already executing!");

        insertRequest(request);
        runTask(new DownloadTask(request));
    }


    private void runTask(DownloadTask task) {
        final DownloadRequest request = task.getRequest();

        Observable.create(task).//TODO add here .cache() or no?
                subscribeOn(workerScheduler).
                observeOn(request.getObserveScheduler()).
                subscribe(new Subscriber<Integer>   () {
                    @Override
                    public void onCompleted() {
                        RequestObserver callback = request.getObserver();
                        if (callback != null)
                            callback.onComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("exc = " + e.getMessage());
                        RequestObserver callback = request.getObserver();
                        if (callback != null)
                            callback.onError((Exception) e);
                    }

                    @Override
                    public void onNext(Integer progress) {
                        RequestObserver callback = request.getObserver();
                        if (callback != null) {
                            if (progress == -1)
                                callback.onInfiniteProgress();
                            else
                                callback.onProgress(progress);
                        }
                    }
                });
    }

    @Override
    public List<DownloadRequest> getQueue() {
        return new ArrayList<>(memoryStorage);
    }
}
