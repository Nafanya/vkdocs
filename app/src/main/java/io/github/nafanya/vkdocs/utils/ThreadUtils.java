package io.github.nafanya.vkdocs.utils;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rx.Scheduler;
import rx.schedulers.Schedulers;

public class ThreadUtils {
    //public static final Scheduler WORK_SCHEDULER = Schedulers.from(Executors.newFixedThreadPool(5));
    public static final ExecutorService WorkerPool = Executors.newFixedThreadPool(5);
}
