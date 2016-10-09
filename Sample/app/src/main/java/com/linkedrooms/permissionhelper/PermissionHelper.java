package com.linkedrooms.permissionhelper;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import java.util.HashMap;

/**
 * Created by michele on 09/10/16.
 */

public class PermissionHelper {
    private static HashMap<String, PermissionListener> permissionListeners = new HashMap<String, PermissionListener>();

    public static abstract class PermissionListener {
        abstract void onPermitted(@NonNull String permission);
        void onForbidden(@NonNull String permission) {
            removePermission(permission);
        }
        boolean shouldTryToMotivate() {
            return false;
        }
        @StringRes int motivationMessage() {
            return 0;
        }
        @StringRes int denialMessage() {
            return 0;
        }
    }

    public static void request(Activity context, @NonNull String permission, PermissionListener callback) {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            callback.onPermitted(permission);
        } else {
            permissionListeners.put(permission, callback);
            ActivityCompat.requestPermissions(context, new String[]{permission}, 500);
        }
    }


    /**
     * used to manually call the motivation system
     */
    public static void motivatePermission(final Activity context, @NonNull final String permission,
                                          @StringRes int need_access, @StringRes int locked_access) {
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
            ad.setMessage(need_access);
            ad.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(context, new String[]{permission}, 500);
                }
            });
            ad.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (permissionListeners.containsKey(permission)) {
                        permissionListeners.get(permission).onForbidden(permission);
                        permissionListeners.remove(permission);
                    }
                }
            });
        } else {
            ad.setMessage(locked_access);
            ad.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (permissionListeners.containsKey(permission)) {
                        permissionListeners.get(permission).onForbidden(permission);
                        permissionListeners.remove(permission);
                    }
                }
            });
        }
        ad.show();
    }

    /**
     * used to manually remove permission request
     */
    public static void removePermission(String permission) {
        if (permissionListeners.containsKey(permission)) {
            permissionListeners.remove(permission);
        }
    }

    public static void onRequestPermissionsResult(Activity context, int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == 500) {
            for (int i = 0; i < grantResults.length; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    for (int j = 0; j < permissionListeners.size(); j++) {
                        if (permissionListeners.containsKey(permission)) {
                            permissionListeners.get(permission).onPermitted(permission);
                            permissionListeners.remove(permission);
                        }
                    }
                } else {
                    if (permissionListeners.containsKey(permission)) {
                        PermissionListener pl = permissionListeners.get(permission);
                        if (pl.shouldTryToMotivate()) {
                            motivatePermission(context, permission, pl.motivationMessage(), pl.denialMessage());
                        } else {
                            permissionListeners.get(permission).onForbidden(permission);
                        }
                    }
                }
            }
        }
    }
}
