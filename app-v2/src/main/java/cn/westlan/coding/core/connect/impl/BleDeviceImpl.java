package cn.westlan.coding.core.connect.impl;

import android.util.Log;
import cn.westlan.coding.core.connect.BleDevice;
import cn.westlan.coding.core.connect.PrintContext;
import com.jakewharton.rx.ReplayingShare;
import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.exceptions.BleAlreadyConnectedException;
import com.polidea.rxandroidble2.exceptions.BleDisconnectedException;
import com.polidea.rxandroidble2.exceptions.BleException;
import com.polidea.rxandroidble2.internal.logger.LoggerUtil;
import io.reactivex.*;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import java.util.ArrayList;
import java.util.List;

public class BleDeviceImpl implements BleDevice {

    private final RxBleDevice bleDevice;
    private final Observable<RxBleConnection> connectionObservable;
    private final PublishSubject<Boolean> disconnectTriggerSubject = PublishSubject.create();
    private final List<Receiver> receivers = new ArrayList<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public BleDeviceImpl(RxBleDevice bleDevice) {
        this.bleDevice = bleDevice;
        this.connectionObservable = prepareConnectionObservable();
    }

    @Override
    public String getName() {
        return bleDevice.getName();
    }

    @Override
    public String getMacAddress() {
        return bleDevice.getMacAddress();
    }

    private Observable<RxBleConnection> prepareConnectionObservable() {
        return bleDevice
                .establishConnection(false)
                .takeUntil(disconnectTriggerSubject)
                .compose(ReplayingShare.instance());
    }

    protected boolean isConnected() {
        return bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED;
    }

    @Override
    public Observable<PrintContext> connect() {
        if(!isConnected()){
            return connectionObservable.map(connection -> {
                if(isConnected()){
                    Disposable notificationDisposable = connection.setupNotification(NOTIFY_CHARACTERISTIC)
                            .flatMap(notificationObservable->notificationObservable).observeOn(Schedulers.single()).subscribe(BleDeviceImpl.this::onNotificationReceived);
                    compositeDisposable.add(notificationDisposable);
                    return (PrintContext)new PrintContextImpl(BleDeviceImpl.this);
                }
                return null;
            }).doFinally(()->compositeDisposable.clear());
        }else{
            BleException exception = new BleAlreadyConnectedException(bleDevice.getMacAddress());
            return Observable.error(exception);
        }
    }

    @Override
    public void disconnect() {
        disconnectTriggerSubject.onNext(true);
    }

    @Override
    public Completable write(byte[] bytes) {
        if (isConnected()) {
            return connectionObservable
                    .firstOrError()
                    .flatMap(rxBleConnection -> rxBleConnection.writeCharacteristic(WRITE_CHARACTERISTIC, bytes))
                    .doOnSuccess(bytes1 -> Log.i(getClass().getSimpleName(), "write bytes success size:"+bytes1.length))
                    .ignoreElement();
        }
        return Completable.error(BleDisconnectedException.adapterDisabled(bleDevice.getMacAddress()));
    }

    @Override
    public void register(Receiver receiver) {
        receivers.add(receiver);
    }

    @Override
    public void unregister(Receiver receiver) {
        receivers.remove(receiver);
    }

    protected void onNotificationReceived(byte[] bytes){
        Log.i(getClass().getSimpleName(), "onNotificationReceived() "+ LoggerUtil.bytesToHex(bytes));
        for(Receiver receiver : receivers){
            try {
                receiver.onReceived(bytes);
            }catch (Throwable throwable){
                Log.e(getClass().getSimpleName(), "onReceived exception", throwable);
            }
        }
    }

}
