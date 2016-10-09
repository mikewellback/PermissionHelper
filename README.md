# PermissionHelper
PermissionHelper.java is a file which will let you handle Android 6.0 permissions system easily.

To start, copy the file PermissionHelper.java into your project.

To request a permission in an Activity, first override the `onRequestPermissionsResult` and call `PermissionHelper.onRequestPermissionsResult` inside:
```
@Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    PermissionHelper.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
}
```

Then, you can require all the permissions you need by simply calling: `PermissionHelper.request([this activity], [permission needed], [permission callback])` this way:
```
PermissionHelper.request(MainActivity.this, Manifest.permission.CAMERA, new PermissionHelper.PermissionListener() {
    @Override void onPermitted(@NonNull String permission) {
        // execute actions
    }
});
```

That's all!

Optionally, you can execute some actions when the permission is forbidden (like closing the Activity or disabling buttons).
This can be done by extending the `onForbidden` method of the PermissionListener:
```
PermissionHelper.request(MainActivity.this, Manifest.permission.CAMERA, new PermissionHelper.PermissionListener() {
    @Override void onPermitted(@NonNull String permission) {
        // execute actions
    }
    
    @Override void onForbidden(@NonNull String permission) {
        super.onForbidden(permission);
        finish();
    }
});
```

Optionally, as stated by [Requesting Permissions at Run Time](https://developer.android.com/training/permissions/requesting.html) page in official guidelines, you can show messages that explain why the permission should be accepted.
To do this, you have to extend `shouldTryToMotivate` and make it returns `true`, then extend `motivationMessage` and `denialMessage` this way:
```
PermissionHelper.request(MainActivity.this, Manifest.permission.CAMERA, new PermissionHelper.PermissionListener() {
    @Override void onPermitted(@NonNull String permission) {
        // execute actions
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
```
`motivationMessage()` is a message which tells the user that he have to grant the permission by pressing OK in the dialog which appears.
The permission will be requested another time and he will have another chance to accept it.
`denialMessage()` is the message which appears when the user totally denied the granting of the permission and it can't be requested anymore.
You have to use string resources, so that you can easily translate those strings in other languages.
Note that when you use this *motivational system*, `onForbidden` will be called only when the user totally refuses to accept permission and to see other permission request of the same kind. Which means, it gets called only when the `denialMessage` is shown.
