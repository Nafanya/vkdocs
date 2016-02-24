package io.github.nafanya.vkdocs.data.exceptions;

import com.vk.sdk.api.VKError;

public class VKException extends Exception {

    private VKError vkError;

    public VKException(VKError vkError) {
        this.vkError = vkError;
    }

    public VKError getVkError() {
        return vkError;
    }
}
