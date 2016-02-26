package io.github.nafanya.vkdocs.domain.download;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import io.github.nafanya.vkdocs.domain.download.base.DownloadManager;
import io.github.nafanya.vkdocs.domain.download.base.RequestStorage;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import timber.log.Timber;

public class InterruptableDownloadManager implements DownloadManager<DownloadRequest> {
    private Scheduler workerScheduler;
    private List<DownloadRequest> queue = new ArrayList<>();
    private int numDownloads;
    private RequestStorage<DownloadRequest> storage;

    public InterruptableDownloadManager(Scheduler workScheduler, RequestStorage<DownloadRequest> storage) {
        this.workerScheduler = workScheduler;
        this.storage = storage;
        queue = storage.getAll();
        Timber.d("queue size %d", queue.size());
        for (DownloadRequest req: queue) {
            if (req.getId() > numDownloads)
                numDownloads = req.getId();
            //Timber.d("down record " + req.getUrl() + " bytes: " + req.getBytes() + " from " + req.getTotalBytes());
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
            if (!isRetry)
                storage.insert(request);
            request.setActive(true);

            HttpURLConnection connection = null;
            try {
                URL url = new URL(request.getUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Range", "bytes=" + request.getBytes() + "-" );
                connection.connect();
                // expect HTTP 206 Partial content, so we don't mistakenly save error report instead of the file
                Timber.d("Server returned HTTP " +
                        connection.getResponseCode() + " " +
                        connection.getResponseMessage());

                if (connection.getResponseCode() != HttpURLConnection.HTTP_PARTIAL && connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    request.setActive(false);
                    subscriber.onError(new RuntimeException("Server returned HTTP " +
                            connection.getResponseCode() + " " +
                            connection.getResponseMessage()));
                    return;
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                if (!isRetry) {
                    fileLength = connection.getContentLength();
                    request.setTotalBytes(fileLength);
                } else
                    fileLength = request.getTotalBytes();

                // download the file
                InputStream input = connection.getInputStream();
                try {
                    FileOutputStream output = new FileOutputStream(request.getDest());
                    FileChannel channel = output.getChannel();

                    try {
                        byte data[] = new byte[4096];
                        long total = request.getBytes();
                        int count;
                        channel.position(total);

                        while ((count = input.read(data)) != -1) {
                            // allow canceling with back button
                            if (request.isCanceled()) {
                                queue.remove(request);
                                storage.delete(request);
                                input.close();
                                return;
                            }
                            total += count;
                            //output.write(data, 0, count);
                            channel.write(ByteBuffer.wrap(data, 0, count));
                            request.setBytes(total);
                            storage.update(request);
                            publishProgress(subscriber, total);
                        }
                        Timber.d("count = " + count);
                        Timber.d("total = " + total);
                        queue.remove(request);
                        storage.delete(request);
                        subscriber.onCompleted();
                    } finally {
                        output.close();
                    }
                } finally {
                    input.close();
                }
            } catch (Exception e) {
                request.setActive(false);
                subscriber.onError(e);
            } finally {
                if (connection != null)
                    connection.disconnect();
            }
        }

        private void publishProgress(Subscriber<? super Integer> subscriber, long total) {
            if (fileLength > 0) {
                int perc = (int) (total * 100 / fileLength);
                if (perc != prevPercentage)
                    subscriber.onNext(perc);
                prevPercentage = perc;
            } else {
                if (prevPercentage != -1)
                    subscriber.onNext(-1);
                prevPercentage = -1;
            }
        }
    }

    public void retry(DownloadRequest request) {
        if (request.getId() == 0)
            throw new RuntimeException("This request isn't executed yet!");
        if (!queue.contains(request))
            throw new RuntimeException("Request doesn't contain in DownloadManager queue!");
        runTask(new DownloadTask(request, true));
    }

    @Override
    public void enqueue(final DownloadRequest request) {
        if (request.getId() != 0)
            throw new RuntimeException("This request already executing!");

        queue.add(request);
        request.setId(++numDownloads);
        runTask(new DownloadTask(request));
    }

    private void runTask(DownloadTask task) {
        final DownloadRequest request = task.getRequest();
        final RequestObserver callback = request.getObserver();
        Observable.create(task).cache().
                subscribeOn(workerScheduler).
                observeOn(request.getScheduler()).
                subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        if (callback != null)
                            callback.onComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (callback != null)
                            callback.onError((Exception) e);
                    }

                    @Override
                    public void onNext(Integer progress) {
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
        return queue;
    }
}
