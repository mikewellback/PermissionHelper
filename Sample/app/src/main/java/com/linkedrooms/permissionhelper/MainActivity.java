package com.linkedrooms.permissionhelper;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Camera cam;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.light_btn).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(final View view) {
                PermissionHelper.request(MainActivity.this, Manifest.permission.CAMERA, new PermissionHelper.PermissionListener() {
                    @Override void onPermitted(@NonNull String permission) {
                        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                            if (view.getTag().equals("off")) {
                                cam = Camera.open();
                                Camera.Parameters p = cam.getParameters();
                                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                cam.setParameters(p);
                                cam.startPreview();
                                view.setTag("on");
                                ((Button) view).setText(R.string.turn_off_light);
                            } else {
                                cam.stopPreview();
                                cam.release();
                                view.setTag("off");
                                ((Button) view).setText(R.string.turn_on_light);
                            }
                        }
                    }

                    /*
                       next functions override is optional.

                       onForbidden() here is used to make the Activity close if permission is not
                       granted

                       shouldTryToMotivate() tells wheter to show messages which explains motivation
                       behind the permission request. If this returns false (default), onForbidden
                       will be called once the permission is forbidden. If true, instead, onForbidden
                       will be called only when the user totally refuses to accept permission and to
                       see other permission request of the same kind

                       motivationMessage() is a message which tells the user that he have to grant
                       the permission by pressing OK in this dialog. Permission will be requested
                       another time and he have another chance

                       denialMessage() is the message which appears when the user totally denied the
                       granting of the permission and it can't be requested anymore
                    */

                    @Override void onForbidden(@NonNull String permission) {
                        super.onForbidden(permission);
                        finish();
                    }

                    @Override boolean shouldTryToMotivate() {
                        return true;
                    }

                    @Override int motivationMessage() {
                        return R.string.motivation_flashlight;
                    }

                    @Override int denialMessage() {
                        return R.string.denial_flashlight;
                    }
                });
            }
        });
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}
