package com.infoline.doctorcha.core.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.infoline.doctorcha.presentation.MainApp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iamkhan on 2017-01-04.
 */

public class PermissionUtil {
    public static final int MY_PERMISSION_REQUEST = 100;

    /*
    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
    //권한요청이 거부되었을 경우 true로 들어온다
    }
    */

    public static boolean requestWriteStoragePermissions(Activity activity) {
        boolean success = true;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final List<String> requestList = new ArrayList<>();

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if(requestList.size() != 0) {
                success = false;
                ActivityCompat.requestPermissions(activity, requestList.toArray(new String[requestList.size()]), MY_PERMISSION_REQUEST);
            }
        }

        return success;
    }

    public static boolean requestReadPhoneState(Activity activity) {
        boolean success = true;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final List<String> requestList = new ArrayList<>();

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                requestList.add(android.Manifest.permission.READ_PHONE_STATE);
            }

            /*
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestList.add(android.Manifest.permission.READ_SMS);
            }
            */

            if(requestList.size() != 0) {
                success = false;
                ActivityCompat.requestPermissions(activity, requestList.toArray(new String[requestList.size()]), MY_PERMISSION_REQUEST);
            }
        }

        return success;
    }

    public static boolean requestGps(Activity activity) {
        boolean success = true;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final List<String> requestList = new ArrayList<>();

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            }

            /*
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestList.add(android.Manifest.permission.READ_SMS);
            }
            */

            if(requestList.size() != 0) {
                success = false;
                ActivityCompat.requestPermissions(activity, requestList.toArray(new String[requestList.size()]), MY_PERMISSION_REQUEST);
            }
        }

        return success;
    }

    public static boolean requestCameraAndWriteStoragePermissions(Activity activity) {
        boolean success = true;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final List<String> requestList = new ArrayList<>();

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestList.add(android.Manifest.permission.CAMERA);
            }

            if(requestList.size() != 0) {
                success = false;
                ActivityCompat.requestPermissions(activity, requestList.toArray(new String[requestList.size()]), MY_PERMISSION_REQUEST);
            }

        }

        return success;
    }
}
