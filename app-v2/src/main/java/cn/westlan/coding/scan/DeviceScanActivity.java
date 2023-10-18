package cn.westlan.coding.scan;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import cn.westlan.coding.R;
import cn.westlan.coding.control.ControlActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint({"NewApi"})
public class DeviceScanActivity extends ListActivity {
    static final int REQUEST_CODE_LOCATION_SETTINGS = 1001;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 4800;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private final Handler mHandlerscan = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 6:
                    DeviceHolder dd = (DeviceHolder) msg.obj;
                    DeviceScanActivity.this.mLeDeviceListAdapter.addDevice(dd.bluedevice, dd.rssi, dd.scanRecord);
                    DeviceScanActivity.this.mLeDeviceListAdapter.notifyDataSetChanged();
                    return;
                case 7:
                    if (DeviceScanActivity.this.mScanning) {
                        DeviceScanActivity.this.scanLeDevice(false);
                        return;
                    }
                    return;
                case 8:
                    if (!DeviceScanActivity.this.mScanning) {
                        DeviceScanActivity.this.scanLeDevice(true);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    };
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private LeScanCallback mLeScanCallback = new LeScanCallback() {
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            DeviceHolder mDeviceHolder = new DeviceHolder();
            mDeviceHolder.bluedevice = device;
            mDeviceHolder.rssi = rssi;
            mDeviceHolder.scanRecord = scanRecord;
            Message message = Message.obtain();
            message.what = 6;
            message.obj = mDeviceHolder;
            DeviceScanActivity.this.mHandlerscan.sendMessage(message);
        }
    };
    private boolean mScanning = false;
    private PowerManager pmscan;

    static class DeviceHolder {
        BluetoothDevice bluedevice;
        int rssi;
        byte[] scanRecord;

        DeviceHolder() {
        }
    }

    private class LeDeviceListAdapter extends BaseAdapter {
        private Timer checkTimer = null;
        private LayoutInflater mInflator;
        private ArrayList<BluetoothDevice> mLeDevices = new ArrayList();
        List<Map<String, String>> mLeDevicesList;

        public LeDeviceListAdapter() {
            this.mInflator = DeviceScanActivity.this.getLayoutInflater();
            this.mLeDevicesList = new ArrayList();
        }

        public void addDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (this.mLeDevices.contains(device)) {
                String nameString = device.getAddress();
                for (int i = 0; i < this.mLeDevicesList.size(); i++) {
                    Map<String, String> de = (Map) this.mLeDevicesList.get(i);
                    if (nameString.equals(de.get("addr"))) {
                        de.remove("name");
                        de.put("name", device.getName());
                        de.remove("rssi");
                        de.put("rssi", Integer.toString(rssi));
                        de.remove("scanRecord");
                        de.put("scanRecord", ProcessData.toHexbyte(scanRecord, scanRecord.length));
                    }
                }
                return;
            }
            this.mLeDevices.add(device);
            HashMap<String, String> contentHashMap = new HashMap();
            contentHashMap.put("name", device.getName());
            contentHashMap.put("addr", device.getAddress());
            contentHashMap.put("rssi", Integer.toString(rssi));
            contentHashMap.put("scanRecord", ProcessData.toHexbyte(scanRecord, scanRecord.length));
            this.mLeDevicesList.add(contentHashMap);
        }

        public void changeRssi(BluetoothDevice device, int rssi) {
            if (this.mLeDevicesList.size() == this.mLeDevices.size()) {
                String nameString = device.getAddress();
                for (int i = 0; i < this.mLeDevicesList.size(); i++) {
                    Map<String, String> de = (Map) this.mLeDevicesList.get(i);
                    if (nameString.equals(de.get("addr"))) {
                        de.remove("rssi");
                        de.put("rssi", Integer.toString(rssi));
                    }
                }
            }
        }

        public BluetoothDevice getDevice(int position) {
            return (BluetoothDevice) this.mLeDevices.get(position);
        }

        public void clear() {
            this.mLeDevices.clear();
            this.mLeDevicesList.clear();
            notifyDataSetChanged();
        }

        public int getCount() {
            return this.mLeDevices.size();
        }

        public Object getItem(int i) {
            return this.mLeDevices.get(i);
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = this.mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                viewHolder.rssi = (TextView) view.findViewById(R.id.rssi);
                viewHolder.scanRecord = (TextView) view.findViewById(R.id.scanRecord);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            Map<String, String> device = (Map) this.mLeDevicesList.get(i);
            String deviceName = (String) device.get("name");
            String deviceaddr = (String) device.get("addr");
            String devicerssi = new StringBuilder(String.valueOf((String) device.get("rssi"))).append("dB").toString();
            String devicescanRecord = "scanRecord:" + ((String) device.get("scanRecord"));
            if (deviceName == null || deviceName.length() <= 0) {
                viewHolder.deviceName.setText("unknown");
            } else {
                viewHolder.deviceName.setText(deviceName);
            }
            viewHolder.deviceAddress.setText(deviceaddr);
            viewHolder.rssi.setText(devicerssi);
            viewHolder.scanRecord.setText(devicescanRecord);
            return view;
        }

        private void checkRssiEnable(boolean en) {
            if (en) {
                if (this.checkTimer == null) {
                    this.checkTimer = new Timer();
                    this.checkTimer.schedule(new TimerTask() {
                        int checkTimercount = 0;

                        public void run() {
                            this.checkTimercount++;
                            if (this.checkTimercount == 48 && DeviceScanActivity.this.mScanning) {
                                DeviceScanActivity.this.scanLeDevice(false);
                            }
                            if (this.checkTimercount == 50) {
                                if (!DeviceScanActivity.this.mScanning) {
                                    DeviceScanActivity.this.scanLeDevice(true);
                                }
                                this.checkTimercount = 0;
                            }
                        }
                    }, 10, 100);
                    DeviceScanActivity.this.scanLeDevice(true);
                    DeviceScanActivity.this.invalidateOptionsMenu();
                }
            } else if (this.checkTimer != null) {
                this.checkTimer.cancel();
                this.checkTimer = null;
                DeviceScanActivity.this.scanLeDevice(false);
                DeviceScanActivity.this.invalidateOptionsMenu();
            }
        }
    }

    static class MViewHolder {
        RelativeLayout mRelativeLayout;

        MViewHolder() {
        }
    }

    static class ViewHolder {
        TextView deviceAddress;
        TextView deviceName;
        TextView rssi;
        TextView scanRecord;

        ViewHolder() {
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        check_location_server();
        this.mHandler = new Handler();
        if (!getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
            Toast.makeText(this, R.string.error_scan_failed_feature_unsupported, Toast.LENGTH_SHORT).show();
            finish();
        }
        this.mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if (this.mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_disabled, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        this.pmscan = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mLeDeviceListAdapter = new LeDeviceListAdapter();
        setListAdapter(this.mLeDeviceListAdapter);
    }

    protected void onResume() {
        super.onResume();
        if (!(this.mBluetoothAdapter.isEnabled() || this.mBluetoothAdapter.isEnabled())) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 1);
        }
        this.mLeDeviceListAdapter.clear();
        this.mLeDeviceListAdapter.checkRssiEnable(true);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 0) {
            finish();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void onPause() {
        super.onPause();
        this.mLeDeviceListAdapter.checkRssiEnable(false);
        this.mLeDeviceListAdapter.clear();
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        System.out.println("==position==" + position);
        BluetoothDevice device = this.mLeDeviceListAdapter.getDevice(position);
        if (device != null) {
            Intent intent = ControlActivity.startActivityIntent(this, device.getAddress(), device.getName());
            if (this.mScanning) {
                this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
                this.mScanning = false;
            }
            startActivity(intent);
        }
    }

    private void scanLeDevice(boolean enable) {
        if (enable) {
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    DeviceScanActivity.this.mScanning = false;
                    DeviceScanActivity.this.mBluetoothAdapter.stopLeScan(DeviceScanActivity.this.mLeScanCallback);
                }
            }, SCAN_PERIOD);
            this.mScanning = true;
            this.mBluetoothAdapter.startLeScan(this.mLeScanCallback);
            return;
        }
        this.mScanning = false;
        this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
    }

    public final boolean isLocationEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean networkProvider = locationManager.isProviderEnabled("network");
        boolean gpsProvider = locationManager.isProviderEnabled("gps");
        if (networkProvider || gpsProvider) {
            return true;
        }
        return false;
    }

    boolean checkWriteExternalPermission1() {
        if (checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    boolean checkWriteExternalPermission2() {
        if (checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    void check_location_server() {
        if (VERSION.SDK_INT < 23) {
            return;
        }
        if (!isLocationEnable(this)) {
            Toast.makeText(this, R.string.error_location_permission_missing, Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"), 1001);
        } else if (!checkWriteExternalPermission1() || !checkWriteExternalPermission2()) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 1001);
        }
    }
}