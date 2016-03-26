package io.github.nafanya.vkdocs.domain.interactor;

import io.github.nafanya.vkdocs.domain.events.EventBus;
import io.github.nafanya.vkdocs.domain.interactor.base.UseCase;
import io.github.nafanya.vkdocs.domain.model.VkDocument;
import io.github.nafanya.vkdocs.net.base.OfflineManager;
import rx.Observable;
import rx.Scheduler;

public class MakeOfflineDocument extends UseCase<VkDocument> {
    private VkDocument document;
    private OfflineManager offlineManager;

    public MakeOfflineDocument(Scheduler observerScheduler, Scheduler subscriberScheduler, EventBus eventBus,
                               OfflineManager offlineManager,
                               VkDocument document) {
        super(observerScheduler, subscriberScheduler, eventBus, true);
        this.offlineManager = offlineManager;
        this.document = document;
    }

    @Override
    public Observable<VkDocument> buildUseCase() {
        return Observable.create(subscriber -> {
            try {
                offlineManager.makeOffline(document, subscriber::onNext);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public int hashCode() {
        return hashByDoc(document);
    }

    private static int P = 1777713;
    private static int MOD = 193843729;
    public static int hashByDoc(VkDocument document) {
        return (int)((document.getId() * 1L * P + 4382321) % MOD);
    }
}
