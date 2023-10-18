package cn.westlan.coding.core.connect;

import cn.westlan.coding.core.bean.*;
import cn.westlan.coding.core.io.Pack;
import cn.westlan.coding.core.io.Readable;
import cn.westlan.coding.core.connect.protocol.*;
import cn.westlan.coding.core.io.Writable;
import io.reactivex.Observable;
import io.reactivex.Single;

import java.io.IOException;

public interface Printer {

    default Single<PrintParams> getPrintParams(){
        return doRequest(Protocol.ReadPrintParams.message(new Empty()), Protocol.ReplyPrintParams);
    }

    default Single<Boolean> setPrintParams(PrintParams printParams){
        return doRequest(Protocol.WritePrintParams.message(printParams), Protocol.ReplyResult).map(Result::isSuccess);
    }

    default Single<Boolean> sendSlaveContent(SlaveContent printContent){
        try {
            Single<Boolean> single = Single.just(true);
            Pack[] packs = printContent.pack(Protocol.WriteSlaveContent.key().length);
            for(Pack pack : packs){
                single = single.flatMap(result->{
                    if(result){
                        return doRequest(Protocol.WriteSlaveContent.message(pack), Protocol.ReplyResult).map(Result::isSuccess);
                    }else{
                        return Single.just(result);
                    }
                });
            }
            return single;
        } catch (IOException e){
            return Single.error(e);
        }
    }


    default Single<Boolean> savePrintContent(PrintContent printContent){
        try {
//            return Observable.fromArray(console.pack(200-Protocol.WritePrintConsole.key().length))
//                    .concatMapSingle(
//                            pack->doRequest(Protocol.WritePrintConsole.message(pack), Protocol.ReplyResult)
//                                    .map(result -> {
//                                        if(!result.isSuccess())throw new RuntimeException("发送失败");
//                                        return true;
//                                    })).single(true).onErrorReturn(throwable -> false);
            Single<Boolean> single = Single.just(true);
            Pack[] packs = printContent.pack(Protocol.WritePrintContent.key().length);
            for(Pack pack : packs){
                single = single.flatMap(result->{
                    if(result){
                        return doRequest(Protocol.WritePrintContent.message(pack), Protocol.ReplyResult).map(Result::isSuccess);
                    }else{
                        return Single.just(result);
                    }
                });
            }
            return single;

        }catch (IOException e){
           return Single.error(e);
        }
    }

    default Single<PrintContent> readPrintContent(){
        return doRequest(Protocol.ReadPrintContent.message(new Empty()), Protocol.ReplyPrintContent);
    }

    default Single<PrintStatus> getPrintStatus(){
        return doRequest(Protocol.ReadPrintStatus.message(new Empty()), Protocol.ReplyPrintStatus);
    }

    default Single<String> getDeviceName(){
        return doRequest(Protocol.ReadDeviceName.message(new Empty()), Protocol.ReplyDeviceName).map(Text::toString);
    }

    default Single<Boolean> setDeviceName(String deviceName){
        return doRequest(Protocol.WriteDeviceName.message(new Text(deviceName)), Protocol.ReplyResult).map(Result::isSuccess);
    }
    
    default Single<String> getProductLine(){
        return doRequest(Protocol.ReadProductLine.message(new Empty()), Protocol.ReplyProductLine).map(Text::toString);
    }

    default Single<Boolean> setProductLine(String productLine){
        return doRequest(Protocol.WriteProductLine.message(new Text(productLine)), Protocol.ReplyResult).map(Result::isSuccess);
    }

    default Single<String> getFactoryNo(){
        return doRequest(Protocol.ReadFactoryNo.message(new Empty()), Protocol.ReplyFactoryNo).map(Text::toString);
    }

    default Single<Boolean> setFactoryNo(String factoryNo){
        return doRequest(Protocol.WriteFactoryNo.message(new Text(factoryNo)), Protocol.ReplyResult).map(Result::isSuccess);
    }

    default Single<Integer> getResistance(){
        return doRequest(Protocol.ReadResistance.message(new Empty()), Protocol.ReplyResistance).map(Number2B::getValue);
    }

    default Single<Boolean> setResistance(short resistance){
        return doRequest(Protocol.WriteResistance.message(new Number2B(resistance)), Protocol.ReplyResult).map(Result::isSuccess);
    }

    default Single<Firmware> getFirmware(){
        return doRequest(Protocol.ReadFirmware.message(new Empty()), Protocol.ReplyFirmware);
    }

    default Single<Identifier> getIdentifier(){
        return doRequest(Protocol.ReadIdentifier.message(new Empty()), Protocol.ReplyIdentifier).map(Identifier::parse);
    }
    
    default Single<String> getSerialNumber(){
        return doRequest(Protocol.ReadSerialNumber.message(new Empty()), Protocol.ReplySerialNumber).map(Text::toString);
    }

    default Single<Boolean> print(){
        return doRequest(Protocol.Print.message(new Empty()), Protocol.ReplyResult).map(Result::isSuccess);
    }

    default Single<Boolean> switchOn(){
        return doRequest(Protocol.SwitchOn.message(new Empty()), Protocol.ReplyResult).map(Result::isSuccess);
    }

    default Single<Boolean> switchOff(){
        return doRequest(Protocol.SwitchOff.message(new Empty()), Protocol.ReplyResult).map(Result::isSuccess);
    }

    default Single<Boolean> syncTime(Time time){
        return doRequest(Protocol.SyncTime.message(time), Protocol.ReplyResult).map(Result::isSuccess);
    }

    default Single<Boolean> unlock(String password){
        return doRequest(Protocol.Unlock.message(new Text(password)), Protocol.ReplyUnlock).map(UnlockResult::isSuccess);
    }

    default Single<Boolean> verifyPassword(String password){
        return doRequest(Protocol.VerifyPassword.message(new Text(password)), Protocol.ReplyResult).map(Result::isSuccess);
    }

    default Single<Boolean> modifyPassword(String password){
        return doRequest(Protocol.ModifyPassword.message(new Text(password)), Protocol.ReplyResult).map(Result::isSuccess);
    }

    default Single<Boolean> checkLocked(){
        return getPrintStatus().map(printStatus -> printStatus.getRunningState().equals(RunningState.Locked));
    }

    Observable<PrintStatus> observeStatusChanges();

    <T extends Writable,R extends Readable> Single<R> doRequest(RequestMsg<T> requestMsg, Reply<R> reply);
}
