package io.github.nafanya.vkdocs.data;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKList;

import io.github.nafanya.vkdocs.data.exceptions.VKException;
import io.github.nafanya.vkdocs.domain.repository.UserRepository;
import io.github.nafanya.vkdocs.utils.Utils;

/**
 * Created by nafanya on 3/20/16.
 */
public class UserRepositoryImpl implements UserRepository {
    @Override
    public VKApiUser getUserInfo() throws VKException {
        VKParameters parameters = new VKParameters();
        parameters.put("fields", "photo_100");
        VKResponse response = Utils.syncVKRequest(VKApi.users().get(parameters));
        VKApiUser user = null;
        try {
            user = ((VKList<VKApiUserFull>)response.parsedModel).get(0);
        } catch (Exception ignore) {
        }
        return user;
    }

    @Override
    public void synchronize() {

    }
}
