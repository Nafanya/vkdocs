package io.github.nafanya.vkdocs.domain.interactor;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.List;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;
import io.github.nafanya.vkdocs.data.database.repository.DatabaseRepository;
import rx.Observable;
import rx.Scheduler;

public class SynchronizeRepositories extends UseCase<Integer> {
    private List<VKApiDocument> documents;
    private DatabaseRepository toRepository;
    private DatabaseRepository fromRepository;

    public SynchronizeRepositories(Scheduler observerScheduler,
                                   Scheduler subscriberScheduler,
                                   EventBus eventBus,
                                   List<VKApiDocument> documents,
                                   DatabaseRepository toRepository) {
        super(observerScheduler, subscriberScheduler, eventBus);
        this.documents = documents;
        this.toRepository = toRepository;
    }

    public SynchronizeRepositories(Scheduler observerScheduler,
                                   Scheduler subscriberScheduler,
                                   EventBus eventBus,
                                   DatabaseRepository fromRepository,
                                   DatabaseRepository toRepository) {
        super(observerScheduler, subscriberScheduler, eventBus);
        this.fromRepository = fromRepository;
        this.toRepository = toRepository;
    }

    @Override
    public Observable<Integer> buildUseCase() {
        return null;
    }
}
