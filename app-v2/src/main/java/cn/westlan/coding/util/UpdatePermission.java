package cn.westlan.coding.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class UpdatePermission {

    private UpdatePermission() {
        // Utility class
    }

    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 9458;


    public static boolean isUpdateRuntimePermissionGranted(final Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
    }

    public static void requestUpdatePermission(final Activity activity) {
        ActivityCompat.requestPermissions(
                activity,
                /*
                 * the below would cause a ArrayIndexOutOfBoundsException on API < 23. Yet it should not be called then as runtime
                 * permissions are not needed and RxBleClient.isScanRuntimePermissionGranted() returns `true`
                 */
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                WRITE_EXTERNAL_STORAGE_REQUEST_CODE
        );
    }

    public static boolean isRequestUpdatePermissionGranted(final int requestCode, final String[] permissions,
                                                             final int[] grantResults) {
        if (requestCode != WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            return false;
        }
        for (int i = 0; i < permissions.length; i++) {
            if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }

        return false;
    }
}
