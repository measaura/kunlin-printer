package cn.westlan.coding.update;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.westlan.coding.control.ControlActivity;
import cn.westlan.coding.update.dfu.DfuService;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import cn.westlan.coding.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

import java.io.File;
import java.util.Locale;

public class UpdateActivity extends AppCompatActivity {

    private static final String MAC_ADDRESS = "macAddress";
    private static final String DEVICE_NAME = "deviceName";
    private static final String FIRMWARE = "firmware";
    private static final String VERSION_URL = "versionUrl";
    private String macAddress;
    private String deviceName;
    private String firmware;
    @BindView(R.id.updateTips)
    AppCompatTextView updateTipsText;
    @BindView(R.id.versionDescription)
    AppCompatTextView versionDescriptionText;
    @BindView(R.id.checkProgressBar)
    ProgressBar checkProgressBar;
    @BindView(R.id.updateAction)
    Button updateActionButton;
    @BindView(R.id.updateStatus)
    AppCompatTextView updateStatusText;
    @BindView(R.id.updateProgressBar)
    ProgressBar updateProgressBar;
    @BindView(R.id.updateProgressText)
    AppCompatTextView updateProgressText;
    private String versionUrl;
    private Version version;
    private boolean dfuCompleted = false;
    private String dfuError;
    private boolean resumed;
    private UIStatus uiStatus;
    

    public static Intent startActivityIntent(Context context, String peripheralMacAddress, String deviceName, String firmware, String versionUrl) {
        Intent intent = new Intent(context, UpdateActivity.class);
        intent.putExtra(MAC_ADDRESS, peripheralMacAddress);
        intent.putExtra(DEVICE_NAME, deviceName);
        intent.putExtra(FIRMWARE, firmware);
        intent.putExtra(VERSION_URL, versionUrl);
//        intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        macAddress = getIntent().getStringExtra(MAC_ADDRESS);
        deviceName = getIntent().getStringExtra(DEVICE_NAME);
        firmware = getIntent().getStringExtra(FIRMWARE);
        versionUrl = getIntent().getStringExtra(VERSION_URL);
        setContentView(R.layout.activity_update);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DfuServiceListenerHelper.registerProgressListener(this, dfuProgressListener);
        updateUI(UIStatus.init);
        checkUpdate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DfuServiceListenerHelper.unregisterProgressListener(this, dfuProgressListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        resumed = true;
        if (dfuCompleted)
            onTransferCompleted();
        if (dfuError != null)
            showErrorMessage(dfuError);
        if (dfuCompleted || dfuError != null) {
            // if this activity is still open and upload process was completed, cancel the notification
            final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(DfuService.NOTIFICATION_ID);
            dfuCompleted = false;
            dfuError = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        resumed = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            Intent intent = ControlActivity.startActivityIntent(this, macAddress, deviceName);
            finish();
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkUpdate(){
        Disposable disposable = UpdateUtil.checkVersion(this.versionUrl, this.firmware)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(d->{
                    updateUI(UIStatus.checking);
                })
                .subscribe(version->{
                    this.version = version;
                    if(version.needUpdate()){
                        updateUI(UIStatus.updatable);
                    }else{
                        updateUI(UIStatus.checked);
                    }
                }, throwable -> {
                    this.version = new Version().version("").description("").needUpdate(false);
                    updateUI(UIStatus.checkFailed);
                    Log.e(getClass().getSimpleName(), "checkUpdate exception", throwable);
                    showToast(getString(R.string.dfu_status_check_failed));});
    }

    @OnClick(R.id.updateAction)
    public void onUpdateClick() {
        if(version!=null&&version.needUpdate()){
            final File dir = new File(getFilesDir().getParentFile().getPath()+"/files/firmware/");
            dir.mkdirs();
            Disposable disposable = UpdateUtil.downloadPackage(dir, version.url())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(d->{
                        updateUI(UIStatus.updating);
                        updateProgressBar.setIndeterminate(true);
                        updateProgressText.setText(R.string.downloading);
                    })
                    .subscribe(packagePath->{
                        final DfuServiceInitiator starter = new DfuServiceInitiator(macAddress)
                                .setDeviceName(deviceName)
                                .setKeepBond(true)
                                .setForceDfu(true)
                                .setPacketsReceiptNotificationsValue(12)
                                .setPrepareDataObjectDelay(400)
                                .setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);
                        starter.setZip(packagePath);
                        starter.start(this, DfuService.class);
                    }, throwable -> {
                        updateUI(UIStatus.updateFailed);
                        Log.e(getClass().getSimpleName(), "downloadPackage exception", throwable);
                        showToast(getString(R.string.dfu_action_update_failed));});

        }else{
            checkUpdate();
        }
    }

    private final DfuProgressListener dfuProgressListener = new DfuProgressListenerAdapter() {
        @Override
        public void onDeviceConnecting(@NonNull final String deviceAddress) {
            updateProgressBar.setIndeterminate(true);
            updateProgressText.setText(R.string.dfu_status_connecting);
        }

        @Override
        public void onDfuProcessStarting(@NonNull final String deviceAddress) {
            updateProgressBar.setIndeterminate(true);
            updateProgressText.setText(R.string.dfu_status_starting);
        }

        @Override
        public void onEnablingDfuMode(@NonNull final String deviceAddress) {
            updateProgressBar.setIndeterminate(true);
            updateProgressText.setText(R.string.dfu_status_switching_to_dfu);
        }

        @Override
        public void onFirmwareValidating(@NonNull final String deviceAddress) {
            updateProgressBar.setIndeterminate(true);
            updateProgressText.setText(R.string.dfu_status_validating);
        }

        @Override
        public void onDeviceDisconnecting(@NonNull final String deviceAddress) {
            updateProgressBar.setIndeterminate(true);
            updateProgressText.setText(R.string.dfu_status_disconnecting);
        }

        @Override
        public void onDfuCompleted(@NonNull final String deviceAddress) {
            updateProgressText.setText(R.string.dfu_status_completed);
            if (resumed) {
                // let's wait a bit until we cancel the notification. When canceled immediately it will be recreated by service again.
                new Handler().postDelayed(() -> {
                    onTransferCompleted();

                    // if this activity is still open and upload process was completed, cancel the notification
                    final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(DfuService.NOTIFICATION_ID);
                }, 200);
            } else {
                // Save that the DFU process has finished
                dfuCompleted = true;
            }
        }

        @Override
        public void onDfuAborted(@NonNull final String deviceAddress) {
            updateProgressText.setText(R.string.dfu_status_aborted);
            // let's wait a bit until we cancel the notification. When canceled immediately it will be recreated by service again.
            new Handler().postDelayed(() -> {
                onUploadCanceled();

                // if this activity is still open and upload process was completed, cancel the notification
                final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(DfuService.NOTIFICATION_ID);
            }, 200);
        }

        @Override
        public void onProgressChanged(@NonNull final String deviceAddress, final int percent,
                                      final float speed, final float avgSpeed,
                                      final int currentPart, final int partsTotal) {
            updateProgressBar.setIndeterminate(false);
            updateProgressBar.setProgress(percent);
            updateProgressText.setText(String.format(Locale.getDefault(), "%d%%", percent));
            if (partsTotal > 1)
                updateStatusText.setText(getString(R.string.dfu_status_uploading_part, currentPart, partsTotal));
            else
                updateStatusText.setText(R.string.dfu_status_uploading);
        }

        @Override
        public void onError(@NonNull final String deviceAddress, final int error, final int errorType, final String message) {
            if (resumed) {
                showErrorMessage(message);

                // We have to wait a bit before canceling notification. This is called before DfuService creates the last notification.
                new Handler().postDelayed(() -> {
                    // if this activity is still open and upload process was completed, cancel the notification
                    final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(DfuService.NOTIFICATION_ID);
                }, 200);
            } else {
                dfuError = message;
            }
        }
    };

    private void showErrorMessage(final String message) {
        updateUI(UIStatus.updateFailed);
        showToast("Upload failed: " + message);
    }

    private void onTransferCompleted() {
        updateUI(UIStatus.updated);
        showToast(R.string.dfu_success);
    }

    public void onUploadCanceled() {
        updateUI(UIStatus.checked);
        showToast(R.string.dfu_aborted);
    }

    private enum UIStatus{
        init,
        checked,
        checking,
        checkFailed,
        updatable,
        updated,
        updating,
        updateFailed;
    }

    private void updateUI(final UIStatus status) {
        if(status.equals(uiStatus))return;
        this.uiStatus = status;
        switch (status){
            case init:
                updateTipsText.setText(R.string.dfu_status_no_update);
                versionDescriptionText.setText("");
                updateActionButton.setClickable(true);
                updateActionButton.setText(R.string.dfu_action_check);
                checkProgressBar.setVisibility(View.GONE);
                updateStatusText.setVisibility(View.INVISIBLE);
                updateProgressBar.setVisibility(View.INVISIBLE);
                updateProgressText.setVisibility(View.INVISIBLE);
            case checking:
                updateTipsText.setText(R.string.dfu_status_checking);
                versionDescriptionText.setText("");
                updateActionButton.setClickable(false);
                updateActionButton.setText(R.string.dfu_action_check);
                checkProgressBar.setVisibility(View.VISIBLE);
                break;
            case checked:
                updateTipsText.setText(R.string.dfu_status_no_update);
                versionDescriptionText.setText("");
                updateActionButton.setClickable(true);
                updateActionButton.setText(R.string.dfu_action_check);
                checkProgressBar.setVisibility(View.GONE);
                break;
            case checkFailed:
                updateTipsText.setText(R.string.dfu_status_check_failed);
                versionDescriptionText.setText("");
                updateActionButton.setClickable(true);
                updateActionButton.setText(R.string.dfu_action_check);
                checkProgressBar.setVisibility(View.GONE);
                break;
            case updatable:
                updateTipsText.setText(String.format("Version %s", version.version()));
                versionDescriptionText.setText(version.description());
                updateActionButton.setClickable(true);
                updateActionButton.setText(R.string.dfu_action_update);
                checkProgressBar.setVisibility(View.GONE);
                break;
            case updated:
                updateActionButton.setClickable(false);
                updateActionButton.setText(R.string.dfu_action_updated);
                updateStatusText.setVisibility(View.INVISIBLE);
                updateProgressBar.setVisibility(View.INVISIBLE);
                updateProgressText.setVisibility(View.INVISIBLE);
                break;
            case updating:
                updateActionButton.setClickable(false);
                updateActionButton.setText(R.string.dfu_action_updating);
                updateStatusText.setVisibility(View.VISIBLE);
                updateProgressBar.setVisibility(View.VISIBLE);
                updateProgressText.setVisibility(View.VISIBLE);
                break;
            case updateFailed:
                updateActionButton.setClickable(false);
                updateActionButton.setText(R.string.dfu_action_update_failed);
                updateStatusText.setVisibility(View.INVISIBLE);
                updateProgressBar.setVisibility(View.INVISIBLE);
                updateProgressText.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void showToast(final String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showToast(final int messageResId) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

}