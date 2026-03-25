package com.dungeonkeys000892.app9711.android;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.dungeonkeys000892.app9711.MainGame;

public class AndroidLauncher extends AndroidApplication {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration =
                new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true;
        initialize(new MainGame(), configuration);
    }
}
