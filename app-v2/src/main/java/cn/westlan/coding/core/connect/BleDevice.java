package cn.westlan.coding.core.connect;

import cn.westlan.coding.core.connect.impl.BleDeviceImpl;
import com.polidea.rxandroidble2.RxBleDevice;
import io.reactivex.Completable;
import io.reactivex.Observable;

import java.util.UUID;

public interface BleDevice {
    UUID SERVICE_UUID = UUID.fromString("9ECAC100-8000-0010-8000-00805F9BC05E");
    UUID WRITE_CHARACTERISTIC = UUID.fromString("9ECAC101-8000-0010-8000-00805F9BC05E");
    UUID NOTIFY_CHARACTERISTIC = UUID.fromString("9ECAC102-8000-0010-8000-00805F9BC05E");

    static Observable<PrintContext> connect(RxBleDevice rxBleDevice) {
        return new BleDeviceImpl(rxBleDevice).connect();
    }

//    static Observable<Boolean> isPrinter(RxBleDevice bleDevice){
//        return bleDevice.establishConnection(false).flatMapSingle(RxBleConnection::discoverServices).take(1).map(services -> {
//            for(BluetoothGattService service : services.getBluetoothGattServices()){
//                if(!service.getUuid().equals(SERVICE_UUID)){
//                    continue;
//                }
//                BluetoothGattCharacteristic writeCharacteristic = service.getCharacteristic(WRITE_CHARACTERISTIC);
//                BluetoothGattCharacteristic notifyCharacteristic = service.getCharacteristic(NOTIFY_CHARACTERISTIC);
//                if(writeCharacteristic == null){
//                    return false;
//                }else if(notifyCharacteristic == null){
//                    return false;
//                }
//                return true;
//            };
//            return false;
//        });
//    }

    String getName();

    String getMacAddress();

    Observable<PrintContext> connect();
    void disconnect();

    Completable write(byte[] bytes);
    void register(Receiver receiver);
    void unregister(Receiver receiver);
    interface Receiver{
        void onReceived(byte[] bytes);
    }
}
