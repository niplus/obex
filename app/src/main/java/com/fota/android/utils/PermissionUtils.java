package com.fota.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class PermissionUtils {

    public static boolean checkPermissions(Context context, String permissions) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ActivityCompat.checkSelfPermission(context, permissions) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    public static void requestPermissions(Fragment fragment, String[] permissions, int requestCode) {
        fragment.requestPermissions(permissions, requestCode);
    }

    public static boolean isPermissionGranted(String requestPermission, @NonNull String[] permissions, @NonNull int[] grantResults) {
        return permissions.length > 0 && requestPermission.equals(permissions[0]) && grantResults.length > 0 && PackageManager.PERMISSION_GRANTED == grantResults[0];
    }

    public static boolean isPermissionGranted(@NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissions.length > 0 && grantResults.length > 0) {
            boolean granted = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (PackageManager.PERMISSION_GRANTED != grantResults[i]) {
                    granted = false;
                }
            }
            return granted;
        }
        return false;
    }
}
