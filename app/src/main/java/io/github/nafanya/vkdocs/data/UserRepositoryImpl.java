package io.github.nafanya.vkdocs.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.httpClient.VKImageOperation;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import io.github.nafanya.vkdocs.R;
import io.github.nafanya.vkdocs.data.exceptions.VKException;
import io.github.nafanya.vkdocs.domain.repository.UserRepository;
import io.github.nafanya.vkdocs.utils.Utils;
import timber.log.Timber;

/**
 * Created by nafanya on 3/20/16.
 */
public class UserRepositoryImpl implements UserRepository {
    private static String FIRST_NAME = "first_name";
    private static String LAST_NAME = "last_name";

    private String avatarFile;
    private SharedPreferences sharedPreferences;

    public UserRepositoryImpl(Context context, String cacheRoot) throws IOException {
        sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_user_repository),
                Context.MODE_PRIVATE);
        avatarFile = cacheRoot + File.separator + "avatar";
        File file = new File(avatarFile);
        if (!file.exists()) {
            file.createNewFile();
            InputStream raw = context.getResources().openRawResource(R.raw.camera);
            copyBitmap(raw, avatarFile);
        }
    }

    @Override
    public VKApiUser getUserInfo() {
        VKApiUser user = new VKApiUser();
        user.first_name = sharedPreferences.getString("first_name", null);
        user.last_name = sharedPreferences.getString("last_name", null);
        user.photo_100 = avatarFile;
        return user;
    }

    @Override
    public void synchronize() throws VKException {
        VKParameters parameters = new VKParameters();
        parameters.put("fields", "photo_100");
        VKResponse response = Utils.syncVKRequest(VKApi.users().get(parameters));
        VKApiUser user = null;
        try {
            user = ((VKList<VKApiUserFull>)response.parsedModel).get(0);
        } catch (Exception ignore) {
        }

        InputStream in = null;
        try {
            in = new URL(user.photo_100).openStream();
            copyBitmap(in, avatarFile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FIRST_NAME, user.first_name);
        editor.putString(LAST_NAME, user.last_name);
        editor.commit();
    }

    private void copyBitmap(InputStream in, String avatarFile) throws IOException {
        Bitmap avatar = BitmapFactory.decodeStream(in);
        FileOutputStream out = new FileOutputStream(avatarFile);
        try {
            avatar.compress(Bitmap.CompressFormat.PNG, 100, out);
        } finally {
            out.close();
        }
    }
}
