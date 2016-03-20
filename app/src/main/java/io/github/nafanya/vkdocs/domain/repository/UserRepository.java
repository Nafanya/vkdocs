package io.github.nafanya.vkdocs.domain.repository;

import com.vk.sdk.api.model.VKApiUser;

import io.github.nafanya.vkdocs.data.exceptions.VKException;

/**
 * Created by nafanya on 3/20/16.
 */
public interface UserRepository {
    VKApiUser getUserInfo() throws VKException;
    void synchronize();
}
