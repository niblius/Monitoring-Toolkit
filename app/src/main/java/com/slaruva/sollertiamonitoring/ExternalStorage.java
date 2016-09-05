package com.slaruva.sollertiamonitoring;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;

public class ExternalStorage {
    private static final String TAG = "ExternalStorage";
    public static final int WRITE_PERMISSION = 1;
    public static final int READ_PEMISSION = 2;


    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static File getAppStorageDir() {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "Sollertia");
        if (!file.exists() && !file.mkdirs())
            Log.e(TAG, "Directory not created " + file.getPath());
        return file;
    }

    public static boolean haveWritePermission(Activity act) {
        return ContextCompat.checkSelfPermission(act,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    public static boolean haveReadPermission(Activity act) {
        return ContextCompat.checkSelfPermission(act,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    public static void requestReadPermission(Activity act) {
        ActivityCompat.requestPermissions(act,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                READ_PEMISSION);
    }

    public static void requestWritePermission(Activity act) {
        ActivityCompat.requestPermissions(act,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                WRITE_PERMISSION);
    }
}
