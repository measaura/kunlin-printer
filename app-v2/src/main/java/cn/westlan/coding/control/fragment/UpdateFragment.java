package cn.westlan.coding.control.fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.ViewModelProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.westlan.coding.R;
import cn.westlan.coding.core.connect.PrintContext;
import cn.westlan.coding.control.ControlViewModel;
import cn.westlan.coding.update.dfu.DfuService;
import cn.westlan.coding.update.Version;
import cn.westlan.coding.util.HttpUtil;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Locale;

//改用activity实现
@Deprecated
public class UpdateFragment extends BaseFragment {

    private PrintContext printContext;
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
    private final String versionUrl = "https://gitee.com/dehui-tto-thirty-two/repository/raw/master/printer/version.json";
    private String firmwareVersion;
    private Version version;
    private boolean dfuCompleted = false;
    private String dfuError;
    private boolean resumed;
    private UIStatus uiStatus;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        DfuServiceListenerHelper.registerProgressListener(this.getActivity(), dfuProgressListener);
        ControlViewModel viewModel = new ViewModelProvider(getActivity()).get(ControlViewModel.class);
        printContext = viewModel.getPrintContext();
        updateUI(UIStatus.init);
        firmwareVersion = viewModel.getFirmware().getVersion();
        checkUpdate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DfuServiceListenerHelper.unregisterProgressListener(this.getActivity(), dfuProgressListener);
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
            final NotificationManager manager = (NotificationManager) UpdateFragment.this.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
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

    private void checkUpdate(){
        Disposable disposable = checkVersion(this.versionUrl, this.firmwareVersion)
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
                    showToast("检查更新失败");});
    }

    @OnClick(R.id.updateAction)
    public void onUpdateClick() {
        if(version!=null&&version.needUpdate()){
            Disposable disposable = downloadPackage(this.getActivity(), version.url())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(d->{
                        updateUI(UIStatus.updating);
                    })
                    .subscribe(packagePath->{
                        printContext.close();
                        final DfuServiceInitiator starter = new DfuServiceInitiator(printContext.getBleDevice().getMacAddress())
                                .setDeviceName(printContext.getBleDevice().getName())
                                .setKeepBond(true)
                                .setForceDfu(true)
                                .setPacketsReceiptNotificationsValue(12)
                                .setPrepareDataObjectDelay(400)
                                .setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);
                        starter.setZip(packagePath);
                        starter.start(this.getActivity(), DfuService.class);
                    }, throwable -> {
                        updateUI(UIStatus.updateFailed);
                        Log.e(getClass().getSimpleName(), "downloadPackage exception", throwable);
                        showToast("上传失败");});

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
                    final NotificationManager manager = (NotificationManager) UpdateFragment.this.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
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
                final NotificationManager manager = (NotificationManager) UpdateFragment.this.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
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
                    final NotificationManager manager = (NotificationManager) UpdateFragment.this.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
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

    private Single<Version> checkVersion(final String checkUrl, final String localVersion){
        return Single.fromCallable(()->{
            InputStream inputStream = HttpUtil.get(checkUrl);
            JSONObject jsonObject = new JSONObject(HttpUtil.readStr(inputStream, "UTF-8"));
            String version = jsonObject.optString("version");
            String url = jsonObject.optString("url");
            String description = jsonObject.optString("description");
            boolean needUpdate = !localVersion.equalsIgnoreCase(version);
            return new Version().version(version).url(url).description(description).needUpdate(needUpdate);
        }).subscribeOn(Schedulers.io());
    }

    private Single<String> downloadPackage(final Context context, final String packageUrl){
        return Single.fromCallable(()->{
            InputStream inputStream = HttpUtil.get(packageUrl);
            String strDir =  context.getFilesDir().getParentFile().getPath()+"/files/";
            String fileName = getFileNameFromUrl(packageUrl);
            File file = new File(strDir, fileName);
            if (inputStream != null) {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                int ch = -1;
                while ((ch = inputStream.read(buf)) != -1) {
                    fileOutputStream.write(buf, 0, ch);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                return file.getAbsolutePath();
            }
            return "";
        }).subscribeOn(Schedulers.io());
    }

    public String getFileNameFromUrl(String url){
        int index = url.lastIndexOf("/");
        if(index > 0){
            String name = url.substring(index + 1).trim();
            if(name.length()>0){
                return name;
            }
        }
        return System.currentTimeMillis() + ".X";
    }

    private void showToast(final String message) {
        Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void showToast(final int messageResId) {
        Toast.makeText(this.getActivity(), messageResId, Toast.LENGTH_SHORT).show();
    }

}