package io.github.nafanya.vkdocs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.vk.sdk.util.VKUtil;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
