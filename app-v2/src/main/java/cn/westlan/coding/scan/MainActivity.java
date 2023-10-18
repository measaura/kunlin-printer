package cn.westlan.coding.scan;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.leancloud.LCUser;
import cn.leancloud.utils.StringUtil;
import cn.westlan.coding.control.ControlActivity;
import cn.westlan.coding.about.InfoActivity;
import cn.westlan.coding.PrinterApplication;
import cn.westlan.coding.R;
import cn.westlan.coding.core.connect.BleDevice;
import cn.westlan.coding.login.LoginActivity;
import cn.westlan.coding.update.ApkInstallReceiver;
import cn.westlan.coding.update.UpdateUtil;
import cn.westlan.coding.util.*;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.king.app.dialog.AppDialog;
import com.king.app.dialog.AppDialogConfig;
import com.king.app.updater.AppUpdater;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.exceptions.BleScanException;
import com.polidea.rxandroidble2.scan.ScanFilter;
import com.polidea.rxandroidble2.scan.ScanResult;
import com.polidea.rxandroidble2.scan.ScanSettings;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import java.io.File;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    final private static Integer requestCode = 0;
    @BindView(R.id.scan)
    Button scanToggleButton;
    @BindView(R.id.scan_results)
    RecyclerView recyclerView;
    @BindView(R.id.action_bar_title)
    TextView tvTitle;
    @BindView(R.id.tvAppVersion)
    TextView tvAppVersion;
    private RxBleClient rxBleClient;
    private Disposable scanDisposable;
    private ScanResultsAdapter resultsAdapter;
    private boolean hasClickedScan = false;
    private boolean hasClickedCheckUpdate = false;

    public static Intent startActivityIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        ButterKnife.bind(this);
        tvAppVersion.setText(String.format("V %s", UpdateUtil.getVersionName(this)));
        rxBleClient = PrinterApplication.getRxBleClient(this);
        if(!rxBleClient.isConnectRuntimePermissionGranted()){
            ConnectPermission.requestConnectionPermission(this, rxBleClient);
        }
        configureResultList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LCUser lcUser = PrinterApplication.getUser(this);
        if(lcUser != null){
            String name = lcUser.get("name").toString();
            if(StringUtil.isEmpty(name)){
                name = "佚名";
            }
            tvTitle.setText(name);
        }
    }

    @OnClick(R.id.action_bar_title)
    public void onTileClick(){
        LCUser lcUser = PrinterApplication.getUser(this);
        if(lcUser != null){
            ConfirmDialog.show(this,"", "是否退出登录", () -> {
                PrinterApplication.setUser(MainActivity.this, null);
                tvTitle.setText("未登录");
            });
        }else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_update) {
            if(UpdatePermission.isUpdateRuntimePermissionGranted(this)){
                checkUpdate();
            }else {
                hasClickedCheckUpdate = true;
                UpdatePermission.requestUpdatePermission(this);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkUpdate(){
        String versionUrl = "https://coding-machine.oss-accelerate.aliyuncs.com/app/android/version.json";
        File dir = new File(getFilesDir().getParentFile().getPath()+"/files/apk/");
        dir.mkdirs();
        final Dialog waitingDialog = WaitingDialog.show(this, getString(R.string.dfu_status_checking));
        Disposable disposable = UpdateUtil.checkVersion(versionUrl, UpdateUtil.getVersionName(this))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(version->{
                    waitingDialog.dismiss();
//                    if(version.needUpdate()){
//                        ConfirmDialog.show(MainActivity.this, "版本升级", version.description(), ()->{
////                            UpdateUtil.installPackage(MainActivity.this, version);
//                            //简单弹框升级
//
//                        });
//                    }else  {
//                        showToast(getString(R.string.dfu_status_no_update));
//                    }
                    if(version.needUpdate()){
                        AppDialogConfig config = new AppDialogConfig(this);
                        config.setTitle("版本升级")
                                .setConfirm("升级") //旧版本使用setOk
                                .setContent(version.description())
                                .setOnClickConfirm(new View.OnClickListener() { //旧版本使用setOnClickOk
                                    @Override
                                    public void onClick(View v) {
                                        new AppUpdater.Builder()
                                                .setUrl(version.url())
                                                .build(MainActivity.this)
                                                .start();
                                        AppDialog.INSTANCE.dismissDialog();
                                    }
                                });
                        AppDialog.INSTANCE.showDialog(MainActivity.this,config);
                    }else  {
                        showToast(getString(R.string.dfu_status_no_update));
                    }
                }, throwable -> {
                    waitingDialog.dismiss();
                    showToast(getString(R.string.dfu_status_check_failed));});
    }


    @OnClick(R.id.qrcode)
    public void onQrcodeClick() {
//        new IntentIntegrator(this).initiateScan(); //初始化扫描

        IntentIntegrator integrator = new IntentIntegrator(this);
        // 设置要扫描的条码类型，ONE_D_CODE_TYPES：一维码，QR_CODE_TYPES-二维码
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setCaptureActivity(ScanActivity.class);
        integrator.setPrompt("扫码连接"); //底部的提示文字，设为""可以置空
        integrator.setCameraId(0); //前置或者后置摄像头
        integrator.setBeepEnabled(false); //扫描成功的「哔哔」声，默认开启
//        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @OnClick(R.id.scan)
    public void onScanToggleClick() {

        if (isScanning()) {
            scanDisposable.dispose();
        } else {
            if (!rxBleClient.isScanRuntimePermissionGranted()) {
                hasClickedScan = true;
                LocationPermission.requestLocationPermission(this, rxBleClient);
            }else if (!rxBleClient.isConnectRuntimePermissionGranted()) {
                hasClickedScan = true;
                ConnectPermission.requestConnectionPermission(this, rxBleClient);
            } else{
                scanBleDevices();
            }

        }

        updateButtonUIState();
    }

    @OnClick(R.id.language)
    public void onLanguageClick(View view) {

        PopupMenu popup = new PopupMenu(this, view);
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.menu_language, popup.getMenu());
        // Setup menu item selection

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.chinese:
                        // 应用用户选择语言
                        setLocale(PrinterApplication.getInstance(), "zh");
                        setLocale(MainActivity.this, "zh");
                        recreate();
                        return true;
                    case R.id.english:
                        // 应用用户选择语言
                        setLocale(PrinterApplication.getInstance(), "en");
                        setLocale(MainActivity.this, "en");
                        recreate();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    public static void setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    private void scanBleDevices() {
        scanDisposable = rxBleClient.scanBleDevices(
                new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                        .build(),
                new ScanFilter.Builder()
                        .setServiceUuid(new ParcelUuid(BleDevice.SERVICE_UUID))
                        .build()
        )
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(this::dispose)
                .subscribe(resultsAdapter::addScanResult, this::onScanFailure);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
            @NonNull final int[] grantResults) {
        if (LocationPermission.isRequestLocationPermissionGranted(requestCode, permissions, grantResults, rxBleClient)
                && hasClickedScan) {
            hasClickedScan = false;
            scanBleDevices();
        }
        if (ConnectPermission.isRequestConnectionPermissionGranted(requestCode, permissions, grantResults, rxBleClient)
                && hasClickedScan) {
            hasClickedScan = false;
            scanBleDevices();
        }
        if (UpdatePermission.isRequestUpdatePermissionGranted(requestCode, permissions, grantResults)
                && hasClickedCheckUpdate) {
            hasClickedCheckUpdate = false;
            checkUpdate();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (isScanning()) {
            /*
             * Stop scanning in onPause callback.
             */
            scanDisposable.dispose();
        }
    }

    private void configureResultList() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(null);
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        resultsAdapter = new ScanResultsAdapter();
        recyclerView.setAdapter(resultsAdapter);
        resultsAdapter.setOnAdapterItemClickListener(view -> {
            final int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
            final ScanResult itemAtPosition = resultsAdapter.getItemAtPosition(childAdapterPosition);
            onAdapterItemClick(itemAtPosition);
        });
    }

    private boolean isScanning() {
        return scanDisposable != null;
    }

    private void onAdapterItemClick(ScanResult scanResult) {
        final String macAddress = scanResult.getBleDevice().getMacAddress();
        final String deviceName = scanResult.getBleDevice().getName();
        Intent intent = ControlActivity.startActivityIntent(this, macAddress, deviceName);
        startActivity(intent);
    }

    private void onScanFailure(Throwable throwable) {
        if (throwable instanceof BleScanException) {
            ScanExceptionHandler.handleException(this, (BleScanException) throwable);
        }
    }

    private void dispose() {
        scanDisposable = null;
        resultsAdapter.clearScanResults();
        updateButtonUIState();
    }

    private void updateButtonUIState() {
        scanToggleButton.setText(isScanning() ? R.string.stop_scan : R.string.start_scan);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //zxing
            if (result.getContents() == null) {
                Toast.makeText(this, getString(R.string.scan_qrcode_canceled), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,  result.getContents(), Toast.LENGTH_LONG).show();//"扫描成功，条码值: " +
            }
        } else {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, getString(R.string.connect_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.disconnection), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showToast(final String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showToast(final int messageResId) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }
}
