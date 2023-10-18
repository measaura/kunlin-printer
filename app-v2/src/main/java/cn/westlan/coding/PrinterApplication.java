package cn.westlan.coding;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import androidx.core.content.res.ResourcesCompat;
import androidx.multidex.MultiDex;
import cn.leancloud.LCUser;
import cn.leancloud.LeanCloud;
import cn.leancloud.utils.StringUtil;
import cn.westlan.coding.update.Version;
import cn.westlan.coding.util.HttpUtil;
import com.polidea.rxandroidble2.LogConstants;
import com.polidea.rxandroidble2.LogOptions;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.exceptions.BleException;
import io.reactivex.Single;
import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import org.json.JSONObject;

import java.io.InputStream;

public class PrinterApplication extends Application {
    private LCUser lcUser = null;
    private RxBleClient rxBleClient;
    private static PrinterApplication instance;
    public static final String CONNECTED_DEVICE_CHANNEL = "connected_device_channel";
    public static final String FILE_SAVED_CHANNEL = "file_saved_channel";
    public static final String PROXIMITY_WARNINGS_CHANNEL = "proximity_warnings_channel";
    public static Typeface normalFont = Typeface.DEFAULT;
    public static Typeface boldFont = Typeface.DEFAULT_BOLD;
    public static Bitmap errorBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    public static Bitmap dragXBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    public static Bitmap dragYBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    /**
     * In practice you will use some kind of dependency injection pattern.
     */
    public static PrinterApplication getInstance(){
        return instance;
    }

    public static RxBleClient getRxBleClient(Context context) {
        PrinterApplication application = (PrinterApplication) context.getApplicationContext();
        return application.rxBleClient;
    }

    public static LCUser getUser(Context context) {
        PrinterApplication application = (PrinterApplication) context.getApplicationContext();
        return application.lcUser;
    }

    public static void setUser(Context context, LCUser lcUser) {
        PrinterApplication application = (PrinterApplication) context.getApplicationContext();
        application.lcUser = lcUser;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        MultiDex.install(this);
        LeanCloud.initialize("02pBWgClU1LrfVvCrczn6nSs-gzGzoHsz","nd4gJrRQfkK3dnCXWqC2F628", "https://02pbwgcl.lc-cn-n1-shared.com");
        rxBleClient = RxBleClient.create(this);
        Single.fromCallable(()->{
            normalFont = ResourcesCompat.getFont(this, R.font.msyh);
            boldFont = ResourcesCompat.getFont(this, R.font.msyhb);
            errorBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_info_error);
            dragXBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_action_drag_x);
            dragYBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_action_drag_y);
            Log.i(getClass().getSimpleName(), "load fonts finished!!!");
            return true;
        }).subscribeOn(Schedulers.io()).subscribe();
        RxBleClient.updateLogOptions(new LogOptions.Builder()
                .setLogLevel(LogConstants.INFO)
                .setMacAddressLogSetting(LogConstants.MAC_ADDRESS_FULL)
                .setUuidsLogSetting(LogConstants.UUIDS_FULL)
                .setShouldLogAttributeValues(true)
                .build()
        );
        RxJavaPlugins.setErrorHandler(throwable -> {
            if (throwable instanceof UndeliverableException && throwable.getCause() instanceof BleException) {
                Log.v("SampleApplication", "Suppressed UndeliverableException: " + throwable.toString());
                return; // ignore BleExceptions as they were surely delivered at least once
            }

            Log.e("SampleApplication", "Unexpected Throwable in RxJavaPlugins error handler: " + throwable.toString());
            // add other custom handlers if needed
//            throw new RuntimeException("Unexpected Throwable in RxJavaPlugins error handler", throwable);
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DfuServiceInitiator.createDfuNotificationChannel(this);

            final NotificationChannel channel = new NotificationChannel(CONNECTED_DEVICE_CHANNEL, getString(R.string.channel_connected_devices_title), NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(getString(R.string.channel_connected_devices_description));
            channel.setShowBadge(false);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            final NotificationChannel fileChannel = new NotificationChannel(FILE_SAVED_CHANNEL, getString(R.string.channel_files_title), NotificationManager.IMPORTANCE_LOW);
            fileChannel.setDescription(getString(R.string.channel_files_description));
            fileChannel.setShowBadge(false);
            fileChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            final NotificationChannel proximityChannel = new NotificationChannel(PROXIMITY_WARNINGS_CHANNEL, getString(R.string.channel_proximity_warnings_title), NotificationManager.IMPORTANCE_LOW);
            proximityChannel.setDescription(getString(R.string.channel_proximity_warnings_description));
            proximityChannel.setShowBadge(false);
            proximityChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            notificationManager.createNotificationChannel(fileChannel);
            notificationManager.createNotificationChannel(proximityChannel);
        }
    }
}
