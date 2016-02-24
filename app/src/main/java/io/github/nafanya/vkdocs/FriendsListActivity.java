package io.github.nafanya.vkdocs;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class FriendsListActivity extends ListActivity {

    private List<String> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        setListAdapter(null);

        VKRequest request = VKApi.friends().get(VKParameters.from(
                VKApiConst.FIELDS, "id,first_name,last_name"));


        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                try {
                    JSONArray jsonArray = response.json.getJSONObject("response").getJSONArray("items");
                    int length = jsonArray.length();
                    friends = new ArrayList<>();
                    for (int i = 0; i < length; i++) {
                        VKApiUser user = new VKApiUser(jsonArray.getJSONObject(i));
                        friends.add(user.first_name + " " + user.last_name);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(FriendsListActivity.this,
                            android.R.layout.simple_list_item_1, friends);
                    setListAdapter(adapter);
                } catch (Exception ignored) {

                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
            }
        });

    }

}
