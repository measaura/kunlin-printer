package cn.westlan.coding.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import cn.westlan.coding.PrinterApplication;
import cn.westlan.coding.R;
import cn.westlan.coding.core.bean.Time;
import cn.westlan.coding.core.connect.BleDevice;
import cn.westlan.coding.core.connect.PrintContext;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import java.util.Date;

public class ControlActivity extends AppCompatActivity {

    private static final String MAC_ADDRESS = "macAddress";
    private static final String DEVICE_NAME = "deviceName";
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String macAddress;
    private String deviceName;
    private ControlViewModel viewModel;
    public static final int RequestCode_Picture = 1;

    public static Intent startActivityIntent(Context context, String peripheralMacAddress, String deviceName) {
        Intent intent = new Intent(context, ControlActivity.class);
        intent.putExtra(MAC_ADDRESS, peripheralMacAddress);
        intent.putExtra(DEVICE_NAME, deviceName);
        return intent;
    }

//    public static Intent startActivityIntentReturn(Context context, String peripheralMacAddress) {
//        Intent intent = new Intent(context, ControlActivity.class);
//        intent.putExtra(MAC_ADDRESS, peripheralMacAddress);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        return intent;
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ControlViewModel.class);
        macAddress = getIntent().getStringExtra(MAC_ADDRESS);
        deviceName = getIntent().getStringExtra(DEVICE_NAME);
        Disposable disposable = BleDevice.connect(PrinterApplication.getRxBleClient(this.getApplicationContext()).getBleDevice(macAddress))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onConnectStateChange, this::onError, this::onClose);
        compositeDisposable.add(disposable);
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        disposable.dispose();
//    }


    @Override
    public void onBackPressed() {
        //禁用回退按键
//        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    public void onClose() {


        Intent intent = new Intent();
        // 设置返回码和返回携带的数据
//        intent.putExtra("respond", "Hello,Alice!I'm Bob.");
        // RESULT_OK就是一个默认值，=-1，它说OK就OK吧
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    public void onError(Throwable e) {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }


    private void onConnectStateChange(PrintContext printContext) {
        //on connected
        if(printContext != null){
            viewModel.setPrintContext(printContext);
            initContext(printContext);
        }
    }

    private void initContext(PrintContext printContext){
        Disposable disposable = printContext.getIdentifier().flatMap(identifier -> {
            viewModel.setIdentifier(identifier);
            return printContext.getFirmware();
        }).flatMap(firmware -> {
            viewModel.setFirmware(firmware);
            return printContext.checkLocked();
        }).flatMap(locked -> {
            viewModel.setLocked(locked);
            return printContext.syncTime(new Time(new Date()));
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{
                    Log.i(getClass().getSimpleName(), "syncTime "+result);
                    setContentView(R.layout.activity_control);
                    ActionBar actionBar = this.getSupportActionBar();
                    actionBar.setTitle(String.format(getString(R.string.control_title), deviceName));
                    Toast.makeText(this, getString(R.string.connected), Toast.LENGTH_SHORT).show();
//                    getSupportActionBar().setDisplayHomeAsUpEnabled(true)
                }, this::onError);
        compositeDisposable.add(disposable);
    }
}