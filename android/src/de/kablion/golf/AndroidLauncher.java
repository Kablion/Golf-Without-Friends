package de.kablion.golf;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.numSamples = 2;
        checkPermissions();
        initialize(new Application(), config);
    }

    public void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};

            int permsRequestCode = 200;
            if (!hasPermissions(perms)) {
                requestPermissions(perms, permsRequestCode);

            }
        }
    }


    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
        return false;
    }


    private boolean hasPermissions(String[] permissions) {
        boolean granted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions)
                if (!(checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED))
                    granted = false;
        } else granted = false;
        return granted;
    }
}
