package io.github.nafanya.vkdocs.utils;

import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import io.github.nafanya.vkdocs.data.exceptions.VKException;

public class Utils {
    public static class Reference<T> {
        public T value;
    }

    public static VKResponse syncVKRequest(VKRequest request) throws VKException {
        final Reference<VKResponse> ret = new Reference<>();
        final Reference<VKException> exception = new Reference<>();

        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                ret.value = response;
            }

            @Override
            public void onError(VKError error) {
                exception.value = new VKException(error);
            }
        });
        if (exception.value == null)
            return ret.value;
        throw exception.value;
    }
}
