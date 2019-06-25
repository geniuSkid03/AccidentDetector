package com.geniuskid.accidentdetector.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;


import com.geniuskid.accidentdetector.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class PermissionsHandler {
    private static final int REQ_CODE_FOR_PERMISSION = 1;
    public static final int REQ_STORAGE_PERMISSION = 2;
    public static final int REQ_LOC_PERMISSION = 2;

    public Activity activity;

    private ArrayList<String> requiredPermissions;
    private ArrayList<String> ungrantedPermissions = new ArrayList<>();

    private AlertDialog permissionHelper;

    private static final String READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String CAMERA = Manifest.permission.CAMERA;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String SEND_SMS = Manifest.permission.SEND_SMS;
    private static final String READ_CONTACTS = Manifest.permission.READ_CONTACTS;



    public PermissionsHandler(Activity activity) {
        this.activity = activity;
        permissionHelper = new AlertDialog.Builder(activity).create();
    }

    public void initStoragePermissions() {
        requiredPermissions = new ArrayList<>();
        requiredPermissions.add(READ_STORAGE);
        requiredPermissions.add(WRITE_STORAGE);
        requiredPermissions.add(CAMERA);
    }

    public void initLocationPermission() {
        requiredPermissions = new ArrayList<>();
        requiredPermissions.add(FINE_LOCATION);
        requiredPermissions.add(COARSE_LOCATION);
    }

    public void initPermissions(String[] permissions) {
        requiredPermissions = new ArrayList<>();
        Collections.addAll(requiredPermissions, permissions);
        AppHelper.printLog("PermissionsHelperActivity (init premissions): " + Arrays.toString(requiredPermissions.toArray()));
    }

    public boolean isAllPermissionGranted(int[] grantResults) {
        for (int i : grantResults) {
            if (i != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public boolean isPermissionAvailable(String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void askPermission(String permission) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, REQ_CODE_FOR_PERMISSION);
    }

    public boolean isAllPermissionAvailable() {
        boolean isAllPermissionAvailable = true;
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                isAllPermissionAvailable = false;
                break;
            }
        }
        return isAllPermissionAvailable;
    }

    public void requestPermissionsIfDenied(final int requestCode) {
        String displayMsg = "";
        switch (requestCode) {
            case REQ_STORAGE_PERMISSION:
                displayMsg = "App needs permission to access your storage and camera!";
                break;
        }
        ungrantedPermissions = getUnGrantedPermissionsList();
        if (canShowPermissionRationaleDialog()) {
            showMessageOKCancel(displayMsg, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PermissionsHandler.this.askAllPermissions(requestCode);
                }
            });
            return;
        }
        askAllPermissions(requestCode);
    }

    public void askAllPermissions(int reqCode) {
        ungrantedPermissions = getUnGrantedPermissionsList();
        if (ungrantedPermissions.size() > 0) {
            AppHelper.printLog("ungranted permissions: " + Arrays.toString(ungrantedPermissions.toArray()));
            ActivityCompat.requestPermissions(activity,
                    ungrantedPermissions.toArray(new String[ungrantedPermissions.size()]), reqCode);
        }
    }

//    public void requestPermissionsIfDenied(final String permission) {
//        if (canShowPermissionRationaleDialog(permission)) {
//            showMessageOKCancel(activity.getResources().getString(R.string.permission_message),
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            askPermission(permission);
//                            //askAllPermissions();
//                        }
//                    });
//            return;
//        }
////        askAllPermissions();
//        askPermission(permission);
//    }

    private boolean canShowPermissionRationaleDialog() {
        boolean shouldShowRationale = false;
        ungrantedPermissions = getUnGrantedPermissionsList();
        for (String permission : ungrantedPermissions) {
            boolean shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
            if (shouldShow) {
                shouldShowRationale = true;
                AppHelper.printLog("can show rationale dialog : true");
                break;
            }
        }
        AppHelper.printLog("can show rationale dialog : false");
        return shouldShowRationale;
    }

    private boolean canShowPermissionRationaleDialog(String permission) {
        boolean shouldShowRationale = false;
        boolean shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
        if (shouldShow) {
            shouldShowRationale = true;
        }
        return shouldShowRationale;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        permissionHelper.setTitle(activity.getString(R.string.permission_title));
        permissionHelper.setMessage(message);
        permissionHelper.setButton(AlertDialog.BUTTON_POSITIVE, activity.getString(R.string.ok), okListener);
        permissionHelper.setButton(AlertDialog.BUTTON_NEGATIVE, activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                activity.finish();
                //AppHelpers.showToast(activity, "Open Settings and grant permission to continue", true);
            }
        });

        if (permissionHelper != null && !permissionHelper.isShowing()) {
            permissionHelper.show();
        }

    }

    public ArrayList<String> getUnGrantedPermissionsList() {
        ArrayList<String> list = new ArrayList<>();
        for (String permission : requiredPermissions) {
            int result = ActivityCompat.checkSelfPermission(activity, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                list.add(permission);
            }
        }
        return list;
    }

    public ArrayList<String> checkUngrantedPermissions(ArrayList<String> requiredPermissions) {
        ArrayList<String> arrayList = new ArrayList<>();
        //array = new String[requiredPermissions.size()];

//        array = requiredPermissions.toArray(new String[requiredPermissions.size()]);

        for (String permission : requiredPermissions) {
            if (!isPermissionAvailable(permission)) {
                arrayList.add(permission);
            }
        }
        return arrayList;
    }

    public String[] getUnGrantedPermissionArray() {
        return getUnGrantedPermissionsList().toArray(new String[getUnGrantedPermissionsList().size()]);
    }

    public boolean isRationaleNeeded() {
        return ungrantedPermissions.size() != 0;
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }

    public boolean isStoragePermissionsAvailable() {
        return (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }


    public void initAllPermissions() {
        requiredPermissions = new ArrayList<>();
        requiredPermissions.add(READ_STORAGE);
        requiredPermissions.add(WRITE_STORAGE);
        requiredPermissions.add(SEND_SMS);
        requiredPermissions.add(FINE_LOCATION);
        requiredPermissions.add(COARSE_LOCATION);
        requiredPermissions.add(READ_CONTACTS);
    }
}
