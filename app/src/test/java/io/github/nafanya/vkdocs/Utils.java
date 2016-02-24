package io.github.nafanya.vkdocs;

import com.vk.sdk.api.model.VKApiDocument;

import java.util.Random;

import io.github.nafanya.vkdocs.data.database.model.VKDocument;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by pva701 on 24.02.16.
 */
public class Utils {

    public static final Random random = new Random(0xB00BC);

    public static int randInt() {
        return random.nextInt(2_000_000_000);
    }

    public static int randInt(int l, int r) {
        return random.nextInt(r - l + 1) + l;
    }

    public static int randInt(int r) {
        return randInt(0, r - 1);
    }

    private static int docId = 0;

    public static VKDocument randVkDocument() {
        int id = ++docId;
        VKDocument doc = new VKDocument();
        doc.setId(id);
        doc.setTitle("rand vkdoc" + id);
        return doc;
    }

    public static VKApiDocument randVkApiDocument() {
        int id = ++docId;
        VKApiDocument doc = new VKApiDocument();
        doc.id = id;
        doc.title = "rand vkapidoc" + id;
        return doc;
    }

    public static class Reference<T> {
        public T value;
    }

    /*public <T> void syncObservable(Observable<T> observable, Subscriber<T> subscriber) {
        final Reference<Exception> exception = new Reference<>();

    }*/
}
